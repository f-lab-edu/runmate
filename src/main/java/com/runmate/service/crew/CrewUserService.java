package com.runmate.service.crew;

import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.domain.dto.crew.CrewUserGetDto;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewUserQueryRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.exception.NotFoundCrewUserException;
import com.runmate.service.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CrewUserService {
    private final CrewUserRepository crewUserRepository;
    private final CrewUserQueryRepository crewUserQueryRepository;
    private final UserRepository userRepository;

    public List<CrewUserGetDto> searchCrewUser(Long crewId, int offset, int limit) {
        return crewUserQueryRepository.findCrewUserOrderByActivity(crewId, offset, limit);
    }

    public void kickOutUser(Long adminCrewUserId, Long kickedCrewUserId) {
        CrewUser adminCrewUser = getCrewUserInRepo(adminCrewUserId);
        checkAdminUser(adminCrewUser);

        CrewUser kickedCrewUser = getCrewUserInRepo(kickedCrewUserId);

        crewUserRepository.delete(kickedCrewUser);
    }

    public void withDrawSelf(Long crewUserId) {
        CrewUser crewUser = getCrewUserInRepo(crewUserId);
        crewUserRepository.delete(crewUser);
    }

    public CrewUser getCrewUserInRepo(Long crewUserId) {
        return crewUserRepository.findById(crewUserId)
                .orElseThrow(NotFoundCrewUserException::new);
    }

    void checkAdminUser(CrewUser crewUser) {
        if (crewUser.getRole() != Role.ADMIN)
            throw new UnAuthorizedException("you are not admin user");
    }
}
