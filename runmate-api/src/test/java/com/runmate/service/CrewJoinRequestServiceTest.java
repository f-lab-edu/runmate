package com.runmate.service;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewJoinRequestRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.crew.CrewJoinRequestService;
import com.runmate.service.exception.BelongToSomeCrewException;
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
public class CrewJoinRequestServiceTest {
    @Autowired
    CrewJoinRequestService crewJoinRequestService;

    @MockBean
    CrewJoinRequestRepository crewJoinRequestRepository;

    @MockBean
    CrewUserRepository crewUserRepository;

    @MockBean
    CrewRepository crewRepository;

    @MockBean
    UserRepository userRepository;

    @Autowired
    TextureFactory textureFactory;

    @Test
    public void When_SendRequest_UNRANKED_User_To_BRONZE_GradeLimit_Crew_Expect_GradeLimitException() {
        final Long crewId = 1L;
        final String email = "Lambda@Lambda.com";
        Crew crew = makeCrewWithGradeLimit(Grade.BRONZE);
        User user = makeUserWithGrade(email, Grade.UNRANKED);

        crewJoinRequestRepositoryWillReturn(Optional.empty());
        crewUserRepositoryWillReturn(Optional.empty());

        crewRepositoryWillReturnCrew(crewId, crew);
        userRepositoryWillReturnUser(email, user);

        //then
        assertThrows(GradeLimitException.class, () -> {
            crewJoinRequestService.sendJoinRequest(crewId, email);
        });
    }

    @Test
    public void When_SendRequest_DIA_User_To_BRONZE_GradeLimit_Crew_Expect_Call_RequestSave() {
        final Long crewId = 1L;
        final String email = "Lambda@Lambda.com";
        Crew crew = makeCrewWithGradeLimit(Grade.BRONZE);
        User user = makeUserWithGrade(email, Grade.DIA);

        crewJoinRequestRepositoryWillReturn(Optional.empty());
        crewUserRepositoryWillReturn(Optional.empty());

        crewRepositoryWillReturnCrew(crewId, crew);
        userRepositoryWillReturnUser(email, user);

        //when
        crewJoinRequestService.sendJoinRequest(crewId, email);

        //then
        verify(crewJoinRequestRepository, atLeastOnce()).save(any(CrewJoinRequest.class));
    }

    @Test
    public void When_SendRequest_Belonging_User_To_Crew_Expect_BelongToSomeCrewException() {
        final Long crewId = 1L;
        final String email = "Lambda@Lambda.com";

        Crew crew = textureFactory.makeCrew(false);
        User user = textureFactory.makeUser(email, false);
        CrewUser crewUser = textureFactory.makeCrewUser(crew, user, false);

        crewJoinRequestRepositoryWillReturn(Optional.empty());
        crewUserRepositoryWillReturn(Optional.of(crewUser));

        crewRepositoryWillReturnCrew(crewId, crew);
        userRepositoryWillReturnUser(email, user);

        assertThrows(BelongToSomeCrewException.class, () -> {
            crewJoinRequestService.sendJoinRequest(crewId, email);
        });
    }

    @Test
    void When_CancelRequest_Expect_Call_delete() {
        final String email = "Lambda@Lambda.com";
        String adminEmail = "admin@admin.com";
        Long crewId = 1L;

        Crew crew = textureFactory.makeCrew(false);
        User adminUser = textureFactory.makeUser(adminEmail, false);
        User user = textureFactory.makeUser(email, false);
        CrewUser admin = textureFactory.makeCrewUser(crew, adminUser, false);
        crew.setId(crewId);

        final Long requestId = 1L;
        CrewJoinRequest request = textureFactory.makeRequest(crew, user, false);

        when(crewJoinRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(crewUserRepository.findAdmin(crewId)).thenReturn(Optional.of(admin));

        //then
        crewJoinRequestService.cancelJoinRequest(adminEmail, crewId, requestId);
        verify(crewJoinRequestRepository, atLeastOnce()).delete(any(CrewJoinRequest.class));
    }

    @Test
    void When_acknowledgeRequest_Expect_Call_crewJoinRequest_Delete_crewUser_Save() {
        final String adminEmail = "admin@admin.com";
        final String email = "Lambda@Lambda.com";
        final Long crewId = 1L;
        Crew crew = textureFactory.makeCrew(false);
        crew.setId(crewId);
        User user = textureFactory.makeUser(email, false);
        User adminUser = textureFactory.makeUser(adminEmail, false);
        CrewUser admin = textureFactory.makeCrewUser(crew, adminUser, false);


        final Long requestId = 1L;
        CrewJoinRequest request = textureFactory.makeRequest(crew, user, false);
        request.setId(requestId);

        when(crewUserRepository.findAdmin(crewId)).thenReturn(Optional.of(admin));
        when(crewJoinRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        crewJoinRequestService.approveJoinRequest(adminEmail, crew.getId(), requestId);

        verify(crewJoinRequestRepository, atLeastOnce()).delete(any(CrewJoinRequest.class));
        verify(crewUserRepository, atLeastOnce()).save(any(CrewUser.class));
    }

    Crew makeCrewWithGradeLimit(Grade grade) {
        return Crew.builder()
                .gradeLimit(grade)
                .name("Lambda Crew!")
                .build();
    }

    User makeUserWithGrade(String email, Grade grade) {
        User user = User.ofGrade()
                .username("Lambda")
                .email(email)
                .grade(grade)
                .build();
        return user;
    }

    void crewRepositoryWillReturnCrew(Long crewId, Crew crew) {
        when(crewRepository.findById(crewId)).thenReturn(Optional.of(crew));
    }

    void userRepositoryWillReturnUser(String email, User user) {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    }

    void crewJoinRequestRepositoryWillReturn(Optional<CrewJoinRequest> request) {
        when(crewJoinRequestRepository.findCrewJoinRequestByCrewAndUser(any(Crew.class), any(User.class))).thenReturn(request);
    }

    void crewUserRepositoryWillReturn(Optional<CrewUser> crewUser) {
        when(crewUserRepository.findByUser(any(User.class))).thenReturn(crewUser);
    }
}
