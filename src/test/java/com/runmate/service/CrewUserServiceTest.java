package com.runmate.service;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.crew.CrewUserService;
import com.runmate.service.exception.UnAuthorizedException;
import com.runmate.texture.TextureFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CrewUserServiceTest {
    @Autowired
    CrewUserService crewUserService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CrewRepository crewRepository;

    @MockBean
    CrewUserRepository crewUserRepository;

    @Autowired
    TextureFactory textureFactory;

    @Test
    void When_kickOutUser_NonAdminUser_Expect_UnAuthorizedException() {
        final Long KICKED_CrewUser_ID = 2L;
        final Long normalUserId = 4000L;
        final Long crewId = 5000L;

        Crew crew = textureFactory.makeCrew(false);
        crew.setId(crewId);

        User normalUser = textureFactory.makeUser("Lambda1@Lambda2.com", false);
        normalUser.setId(normalUserId);

        User kickedUser = textureFactory.makeUser("kicked@kicked.com", false);

        when(userRepository.findByEmail(normalUser.getEmail())).thenReturn(Optional.of(normalUser));
        when(crewRepository.findById(crew.getId())).thenReturn(Optional.of(crew));
        when(crewUserRepository.findByCrewAndUser(crew, normalUser)).thenReturn(Optional.of(makeNormalCrewUser(crew, normalUser)));
        when(crewUserRepository.findById(KICKED_CrewUser_ID)).thenReturn(Optional.of(makeNormalCrewUser(crew, kickedUser)));

        assertThrows(UnAuthorizedException.class, () -> {
            crewUserService.delete(crewId, KICKED_CrewUser_ID, normalUser.getEmail());
        });
    }

    @Test
    void When_kickOutUser_AdminUser_Expect_Call_Delete() {
        final Long KICKED_CrewUser_ID = 2L;
        final Long crewId = 5000L;

        Crew crew = textureFactory.makeCrew(false);
        crew.setId(crewId);

        User normalUser = textureFactory.makeUser("Lambda1@Lambda2.com", false);
        User kickedUser = textureFactory.makeUser("kicked@kicked.com", false);

        CrewUser wantToDelete = makeNormalCrewUser(crew, kickedUser);
        wantToDelete.setId(KICKED_CrewUser_ID);

        when(userRepository.findByEmail(normalUser.getEmail())).thenReturn(Optional.of(normalUser));
        when(crewRepository.findById(crew.getId())).thenReturn(Optional.of(crew));
        when(crewUserRepository.findByCrewAndUser(crew, normalUser)).thenReturn(Optional.of(makeAdminCrewUser(crew, normalUser)));
        when(crewUserRepository.findById(KICKED_CrewUser_ID)).thenReturn(Optional.of(wantToDelete));

        crewUserService.delete(crewId, KICKED_CrewUser_ID, normalUser.getEmail());
        verify(crewUserRepository, atLeastOnce()).delete(wantToDelete);
    }

    @Test
    void When_withDrawSelf_Expect_Call_Delete() {
        final String email = "Lambda@Lambda.com";
        final Long crewUserId = 1L;
        final Long userId = 1L;
        final Long crewId = 1L;

        Crew crew = textureFactory.makeCrew(false);
        crew.setId(crewId);

        User user = textureFactory.makeUser(email, false);
        user.setId(userId);

        CrewUser crewUser = textureFactory.makeCrewUser(crew, user, false);
        crewUser.setId(crewUserId);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(crewRepository.findById(crewId)).thenReturn(Optional.of(crew));
        when(crewUserRepository.findById(crewId)).thenReturn(Optional.of(crewUser));
        when(crewUserRepository.findByCrewAndUser(crew, user)).thenReturn(Optional.of(crewUser));

        crewUserService.delete(crewId, crewUserId, email);
        verify(crewUserRepository).delete(any(CrewUser.class));
    }

    CrewUser makeAdminCrewUser(Crew crew, User user) {
        return CrewUser.builder()
                .crew(crew)
                .user(user)
                .role(Role.ADMIN)
                .build();
    }

    CrewUser makeNormalCrewUser(Crew crew, User user) {
        return CrewUser.builder()
                .crew(crew)
                .user(user)
                .role(Role.NORMAL)
                .build();
    }
}
