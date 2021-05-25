package com.runmate.service;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewUserRepository;
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
    CrewUserRepository crewUserRepository;

    @Autowired
    TextureFactory textureFactory;

    @Test
    void When_kickOutUser_NonAdminUser_Expect_UnAuthorizedException() {
        final Long NOT_ADMIN_CrewUser_ID = 1L;
        final Long KICKED_CrewUser_ID = 2L;

        Crew crew = textureFactory.makeCrew(false);
        User normalUser = textureFactory.makeUser("Lambda1@Lambda2.com", false);
        User kickedUser = textureFactory.makeUser("kicked@kicked.com", false);

        when(crewUserRepository.findById(NOT_ADMIN_CrewUser_ID)).thenReturn(Optional.of(makeNormalCrewUser(crew, normalUser)));
        when(crewUserRepository.findById(KICKED_CrewUser_ID)).thenReturn(Optional.of(makeNormalCrewUser(crew, kickedUser)));

        assertThrows(UnAuthorizedException.class, () -> {
            crewUserService.kickOutUser(NOT_ADMIN_CrewUser_ID, KICKED_CrewUser_ID);
        });
    }

    @Test
    void When_kickOutUser_AdminUser_Expect_Call_Delete() {
        final Long ADMIN_CrewUser_ID = 1L;
        final Long KICKED_CrewUser_ID = 2L;

        Crew crew = textureFactory.makeCrew(false);
        User normalUser = textureFactory.makeUser("Lambda1@Lambda2.com", false);
        User kickedUser = textureFactory.makeUser("kicked@kicked.com", false);

        CrewUser wantToDelete = makeNormalCrewUser(crew, kickedUser);

        when(crewUserRepository.findById(ADMIN_CrewUser_ID)).thenReturn(Optional.of(makeAdminCrewUser(crew, normalUser)));
        when(crewUserRepository.findById(KICKED_CrewUser_ID)).thenReturn(Optional.of(wantToDelete));

        crewUserService.kickOutUser(ADMIN_CrewUser_ID, KICKED_CrewUser_ID);
        verify(crewUserRepository, atLeastOnce()).delete(wantToDelete);
    }

    @Test
    void When_withDrawSelf_Expect_Call_Delete() {
        final String email = "Lambda@Lambda.com";
        final Long crewId = 1L;
        Crew crew = textureFactory.makeCrew(false);
        User user = textureFactory.makeUser(email, false);
        CrewUser crewUser = textureFactory.makeCrewUser(crew, user, false);

        when(crewUserRepository.findById(crewId)).thenReturn(Optional.of(crewUser));

        crewUserService.withDrawSelf(crewId);
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
