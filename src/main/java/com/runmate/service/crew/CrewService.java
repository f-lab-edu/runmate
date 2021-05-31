package com.runmate.service.crew;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.dto.crew.CrewGetDto;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewQueryRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.spec.CrewOrderSpec;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.exception.BelongToSomeCrewException;
import com.runmate.service.exception.GradeLimitException;
import com.runmate.service.exception.NotFoundCrewException;
import com.runmate.service.exception.NotFoundCrewUserException;
import com.runmate.service.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CrewService {
    private final CrewRepository crewRepository;
    private final CrewQueryRepository crewQueryRepository;
    private final CrewUserRepository crewUserRepository;
    private final UserRepository userRepository;

    public Crew createCrew(Crew crew, String email) {
        User user = userRepository.findByEmail(email);

        checkCanCreateCrew(crew, user);

        crewUserRepository.save(makeAdminUser(crew, user));
        return crewRepository.save(crew);
    }

    private void checkCanCreateCrew(Crew crew, User user) {
        if (!user.isGradeHigherOrEqualThanCrewGradeLimit(crew))
            throw new GradeLimitException("Admin's grade less then crew's Grade Limit");
        checkBelongToSomeCrew(user);
    }

    private void checkBelongToSomeCrew(User user) {
        crewUserRepository.findByUser(user)
                .ifPresent(crewUser -> {
                    throw new BelongToSomeCrewException("you have already belong to crew:" + crewUser.getCrew().getName());
                });
    }

    private CrewUser makeAdminUser(Crew crew, User user) {
        return CrewUser.builder()
                .crew(crew)
                .user(user)
                .role(Role.ADMIN)
                .build();
    }

    public void deleteCrew(long crewId, String email) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(NotFoundCrewException::new);
        User deleteRequestedUser = userRepository.findByEmail(email);

        CrewUser crewUser = crewUserRepository.findByCrewAndUser(crew, deleteRequestedUser)
                .orElseThrow(() -> new NotFoundCrewUserException("you are not member of the given crew"));

        if (crewUser.isNormal()) {
            throw new UnAuthorizedException("only admin can request to delete crew");
        }

        crewRepository.delete(crew);
    }

    public List<CrewGetDto> searchCrewByRegionOrderByActivityWithPageable(Region region, int offset, int limit, CrewOrderSpec orderSpec) {
        return crewQueryRepository.findByLocationWithSorted(region, PageRequest.of(offset, limit), orderSpec);
    }
}
