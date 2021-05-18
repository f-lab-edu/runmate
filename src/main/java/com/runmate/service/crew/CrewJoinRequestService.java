package com.runmate.service.crew;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewJoinRequestRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.springframework.data.domain.Sort.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CrewJoinRequestService {
    private final CrewJoinRequestRepository crewJoinRequestRepository;
    private final CrewUserRepository crewUserRepository;
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;

    public void sendJoinRequest(Long crewId, String email) {
        User user = userRepository.findByEmail(email);
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(NotFoundCrewException::new);

        if (canSendRequest(crew, user)) {
            CrewJoinRequest request = CrewJoinRequest.builder()
                    .user(user)
                    .crew(crew)
                    .build();
            crewJoinRequestRepository.save(request);
        }
    }

    public List<CrewJoinRequest> searchJoinRequestByCrewWithPageable(Long crewId, int offset, int Limit) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(NotFoundCrewException::new);

        return crewJoinRequestRepository.findAllByCrewWithPageable(crew,
                PageRequest.of(offset, Limit, by(Direction.DESC, "createdAt")));
    }

    public void cancelJoinRequest(Long crewJoinRequestId) {
        CrewJoinRequest request = crewJoinRequestRepository.findById(crewJoinRequestId)
                .orElseThrow(NotFoundCrewJoinRequestException::new);

        crewJoinRequestRepository.delete(request);
    }

    public void acknowledgeJoinRequest(Long crewJoinRequestId) {
        CrewJoinRequest request = crewJoinRequestRepository.findById(crewJoinRequestId)
                .orElseThrow(NotFoundCrewJoinRequestException::new);

        CrewUser crewUser = CrewUser.builder()
                .user(request.getUser())
                .crew(request.getCrew())
                .role(Role.NORMAL)
                .build();

        crewJoinRequestRepository.delete(request);
        crewUserRepository.save(crewUser);
    }

    private boolean canSendRequest(Crew crew, User user) {
        checkGradeLimit(crew, user);
        checkDuplicatedRequestToSameCrew(crew, user);
        checkBelongToSomeCrew(crew, user);
        return true;
    }

    private void checkGradeLimit(Crew crew, User user) {
        if (!user.getGrade().higherOrEqualThan(crew.getGradeLimit()))
            throw new GradeLimitException("Your score is lower than the score limit set by the crew.");
    }

    private void checkDuplicatedRequestToSameCrew(Crew crew, User user) {
        crewJoinRequestRepository.findCrewJoinRequestByCrewAndUser(crew, user)
                .ifPresent(request -> {
                    throw new DuplicatedCrewJoinRequestToSameCrewException("You have already sent a CrewJoinRequest:" + request.getCrew().getName());
                });
    }

    private void checkBelongToSomeCrew(Crew crew, User user) {
        crewUserRepository.findByCrewAndUser(crew, user)
                .ifPresent(crewUser -> {
                    throw new BelongToSomeCrewException("you have already belong to crew:" + crewUser.getCrew().getName());
                });
    }
}
