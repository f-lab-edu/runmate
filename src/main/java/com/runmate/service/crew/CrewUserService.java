package com.runmate.service.crew;

import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.dto.crew.CrewUserGetDto;
import com.runmate.repository.crew.CrewUserQueryRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.service.exception.NotFoundCrewUserException;
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

    public List<CrewUserGetDto> searchCrewUser(Long crewId, int offset, int limit) {
        return crewUserQueryRepository.findCrewUserOrderByActivity(crewId, offset, limit);
    }

    public void withDrawUser(Long crewUserId) {
        CrewUser crewUser = crewUserRepository.findById(crewUserId)
                .orElseThrow(NotFoundCrewUserException::new);
        crewUserRepository.delete(crewUser);
    }
}
