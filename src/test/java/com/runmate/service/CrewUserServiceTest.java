package com.runmate.service;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewUserQueryRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.service.crew.CrewUserService;
import com.runmate.texture.TextureFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

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
    void When_withDrawUser_Expect_CallDelete() {
        final String email = "Lambda@Lambda.com";
        final Long crewId=1L;
        Crew crew = textureFactory.makeCrew(false);
        User user = textureFactory.makeUser(email, false);
        CrewUser crewUser = textureFactory.makeCrewUser(crew, user, false);

        when(crewUserRepository.findById(crewId)).thenReturn(Optional.of(crewUser));

        crewUserService.withDrawUser(crewId);
        verify(crewUserRepository).delete(any(CrewUser.class));
    }
}
