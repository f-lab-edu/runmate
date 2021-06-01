package com.runmate.service.crew;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.dto.crew.CrewJoinRequestGetDto;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewJoinRequestRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CrewJoinRequest sendJoinRequest(Long crewId, String email) {
        User user = userRepository.findByEmail(email);
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(NotFoundCrewException::new);

        checkCanSendRequest(crew, user);
        CrewJoinRequest request = CrewJoinRequest.builder()
                .user(user)
                .crew(crew)
                .build();

        return crewJoinRequestRepository.save(request);

    }

    @Transactional(readOnly = true)
    public List<CrewJoinRequestGetDto> showJoinRequestByCrewWithPageable(Long crewId, String email, int offset, int Limit) {
        User showRequestUser = userRepository.findByEmail(email);
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(NotFoundCrewException::new);

        checkBrowsingJoinRequestsAuthorization(crewId, showRequestUser, crew);

        return crewJoinRequestRepository.findAllByCrewWithPageable(crew,
                PageRequest.of(offset, Limit, by(Direction.DESC, "createdAt")));
    }

    private void checkBrowsingJoinRequestsAuthorization(Long crewId, User user, Crew crew) {
        if (!user.isMemberOfCrew(crewId) || user.getCrewUser().isNormal()) {
            throw new UnAuthorizedException("you can't browse for " + crew.getName() + " requests");
        }
    }

    public CrewUser approveJoinRequest(String email, Long crewId, Long crewJoinRequestId) {
        CrewJoinRequest request = crewJoinRequestRepository.findById(crewJoinRequestId)
                .orElseThrow(NotFoundCrewJoinRequestException::new);

        CrewUser admin = crewUserRepository.findAdmin(crewId).orElseThrow(NotFoundCrewException::new);
        checkRightRequestForProcessingJoinRequests(crewId, request);
        checkAuthorizationForHandlingJoinRequest(email, admin);

        CrewUser crewUser = CrewUser.builder()
                .user(request.getUser())
                .crew(request.getCrew())
                .role(Role.NORMAL)
                .build();

        crewJoinRequestRepository.delete(request);
        return crewUserRepository.save(crewUser);
    }

    public void cancelJoinRequest(String email, Long crewId, Long crewJoinRequestId) {
        CrewJoinRequest request = crewJoinRequestRepository.findById(crewJoinRequestId)
                .orElseThrow(NotFoundCrewJoinRequestException::new);

        CrewUser admin = crewUserRepository.findAdmin(crewId).orElseThrow(NotFoundCrewException::new);
        checkRightRequestForProcessingJoinRequests(crewId, request);
        checkAuthorizationForHandlingJoinRequest(email, admin);

        checkRightRequestForProcessingJoinRequests(crewId, request);

        crewJoinRequestRepository.delete(request);
    }

    private void checkRightRequestForProcessingJoinRequests(Long crewId, CrewJoinRequest request) {
        if (!request.isRequestForCrew(crewId)) {
            throw new IllegalArgumentException("request is not matched to crew");
        }
    }

    private void checkAuthorizationForHandlingJoinRequest(String email, CrewUser admin) {
        if (!admin.isEqualEmail(email)) {
            throw new UnAuthorizedException("you can't handle join requests for this crew");
        }
    }

    private void checkCanSendRequest(Crew crew, User user) {
        if (!user.isGradeHigherOrEqualThanCrewGradeLimit(crew)) {
            throw new GradeLimitException("User's Grade lower than Crew's Grade Limit");
        }
        checkDuplicatedRequestToSameCrew(crew, user);
        checkBelongToSomeCrew(crew, user);
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
