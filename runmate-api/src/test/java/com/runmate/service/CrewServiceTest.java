package com.runmate.service;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewQueryRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.crew.CrewService;
import com.runmate.service.exception.GradeLimitException;
import com.runmate.texture.TextureFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
public class CrewServiceTest {
    @Autowired
    CrewService crewService;
    @Autowired
    TextureFactory textureFactory;
    @MockBean
    CrewRepository crewRepository;
    @MockBean
    CrewQueryRepository crewQueryRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    CrewUserRepository crewUserRepository;

    @Test
    void When_createCrew_Expect_crewRepoSave_Call_crewUserRepoSave_Call() {
        final String email = "Lambda@Lambda.com";
        Crew crew = textureFactory.makeCrew(false);
        crew.setGradeLimit(Grade.UNRANKED);

        User user = textureFactory.makeUser(email, false);
        user.setGrade(Grade.RUBY);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(crewUserRepository.findByCrewAndUser(crew, user)).thenReturn(Optional.empty());

        crewService.createCrew(crew, email);

        verify(crewRepository, atLeastOnce()).save(any(Crew.class));
        verify(crewUserRepository, atLeastOnce()).save(any(CrewUser.class));
    }

    @Test
    void When_createDiaCrew_WithUnRankedUser_Expect_GradeLimitException() {
        final String email = "Lambda@Lambda.com";
        Crew crew = textureFactory.makeCrew(false);
        crew.setGradeLimit(Grade.DIA);

        User user = textureFactory.makeUser(email, false);
        user.setGrade(Grade.UNRANKED);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(crewUserRepository.findByCrewAndUser(crew, user)).thenReturn(Optional.empty());

        assertThrows(GradeLimitException.class, () -> {
            crewService.createCrew(crew, email);
        });
    }
}
