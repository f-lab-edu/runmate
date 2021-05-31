package com.runmate.service.crew;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.user.User;
import com.runmate.dto.crew.CrewUserGetDto;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserQueryRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.spec.CrewUserOrderSpec;
import com.runmate.repository.user.UserRepository;
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
public class CrewUserService {
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;
    private final CrewUserRepository crewUserRepository;
    private final CrewUserQueryRepository crewUserQueryRepository;

    public List<CrewUserGetDto> searchCrewUser(Long crewId, int offset, int limit, CrewUserOrderSpec crewUserOrderSpec) {
        return crewUserQueryRepository.findCrewUserWithSorted(crewId, PageRequest.of(offset, limit), crewUserOrderSpec);
    }

    public void delete(Long crewId, Long deletedCrewUserId, String email) {
        User deleteRequestUser = userRepository.findByEmail(email);
        Crew crew = crewRepository.findById(crewId).orElseThrow(NotFoundCrewException::new);

        CrewUser deletedMember = crewUserRepository.findById(deletedCrewUserId).orElseThrow(NotFoundCrewUserException::new);
        CrewUser deleteRequestMember = crewUserRepository.findByCrewAndUser(crew, deleteRequestUser).orElseThrow(NotFoundCrewUserException::new);
        checkAuthorization(deleteRequestMember, deletedMember);

        crewUserRepository.delete(deletedMember);
    }

    private void checkAuthorization(CrewUser requestUser, CrewUser deletedMember) {
        if (requestUser.isNormal() && !requestUser.equals(deletedMember)) {
            throw new UnAuthorizedException("not authorized to delete " + deletedMember.getId() + " member");
        }
    }
}
