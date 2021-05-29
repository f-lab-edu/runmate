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

    public List<CrewGetDto> searchCrewByRegionOrderByActivityWithPageable(Region region, int offset, int limit, CrewOrderSpec orderSpec) {
        return crewQueryRepository.findByLocationWithSorted(region, PageRequest.of(offset, limit), orderSpec);
    }

    private void checkCanCreateCrew(Crew crew, User user) {
        user.checkGradeHigherThenCrewLimit(crew);
        checkBelongToSomeCrew(crew, user);
    }

    private CrewUser makeAdminUser(Crew crew, User user) {
        return CrewUser.builder()
                .crew(crew)
                .user(user)
                .role(Role.ADMIN)
                .build();
    }

    private void checkBelongToSomeCrew(Crew crew, User user) {
        crewUserRepository.findByCrewAndUser(crew, user)
                .ifPresent(crewUser -> {
                    throw new BelongToSomeCrewException("you have already belong to crew:" + crewUser.getCrew().getName());
                });
    }
}
