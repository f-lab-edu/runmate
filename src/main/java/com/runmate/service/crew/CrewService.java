package com.runmate.service.crew;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.domain.dto.crew.CrewGetDto;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewQueryRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.spec.CrewOrderSpec;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.exception.BelongToSomeCrewException;
import com.runmate.service.exception.GradeLimitException;
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

    public void createCrew(Crew crew, String email) {
        User user = userRepository.findByEmail(email);

        checkCanCreateCrew(crew, user);

        crewRepository.save(crew);
        crewUserRepository.save(makeAdminUser(crew, user));
    }

    public List<CrewGetDto> searchCrewByRegionOrderByActivityWithPageable(Region region, int offset, int limit, CrewOrderSpec orderSpec) {
        return crewQueryRepository.findByLocationWithSorted(region, PageRequest.of(offset, limit), orderSpec);
    }

    private void checkCanCreateCrew(Crew crew, User user) {
        if (!user.isGradeHigherOrEqualThanCrewGradeLimit(crew))
            throw new GradeLimitException("Admin's grade less then crew's Grade Limit");
        checkBelongToSomeCrew(crew, user);
    }

    private CrewUser makeAdminUser(Crew crew, User user) {
        CrewUser crewUser = CrewUser.builder()
                .crew(crew)
                .user(user)
                .role(Role.ADMIN)
                .build();
        return crewUser;
    }

    private void checkBelongToSomeCrew(Crew crew, User user) {
        crewUserRepository.findByCrewAndUser(crew, user)
                .ifPresent(crewUser -> {
                    throw new BelongToSomeCrewException("you have already belong to crew:" + crewUser.getCrew().getName());
                });
    }
}
