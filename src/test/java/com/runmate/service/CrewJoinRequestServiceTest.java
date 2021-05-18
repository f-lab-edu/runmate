package com.runmate.service;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewJoinRequestRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.service.crew.CrewJoinRequestService;
import com.runmate.service.exception.BelongToSomeCrewException;
import com.runmate.service.exception.GradeLimitException;
import com.runmate.texture.TextureFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CrewJoinRequestServiceTest {
    @Autowired
    CrewJoinRequestService crewJoinRequestService;

    @MockBean
    CrewJoinRequestRepository crewJoinRequestRepository;

    @MockBean
    CrewUserRepository crewUserRepository;

    @Autowired
    TextureFactory textureFactory;

    @Test
    public void When_SendRequest_UNRANKED_User_To_BRONZE_GradeLimit_Crew_Expect_GradeLimitException() {
        //설정된 크루x, Request 보낸적도 없는 유저로 설정.
        when(crewJoinRequestRepository.findCrewJoinRequestByCrewAndUser(any(Crew.class), any(User.class))).thenReturn(null);
        when(crewUserRepository.findByCrewAndUser(any(Crew.class), any(User.class))).thenReturn(null);

        Crew crew = makeCrewWithGradeLimit(Grade.BRONZE);
        User user = makeUserWithGrade(Grade.UNRANKED);

        //then
        assertThrows(GradeLimitException.class, () -> {
            crewJoinRequestService.sendJoinRequest(crew, user);
        });
    }

    @Test
    public void When_SendRequest_DIA_User_To_BRONZE_GradeLimit_Crew_Expect_Call_RequestSave() {
        //설정된 크루x, Request 보낸적도 없는 유저로 설정.
        when(crewJoinRequestRepository.findCrewJoinRequestByCrewAndUser(any(Crew.class), any(User.class))).thenReturn(Optional.empty());
        when(crewUserRepository.findByCrewAndUser(any(Crew.class), any(User.class))).thenReturn(Optional.empty());

        Crew crew = makeCrewWithGradeLimit(Grade.BRONZE);
        User user = makeUserWithGrade(Grade.DIA);

        //when
        crewJoinRequestService.sendJoinRequest(crew, user);

        //then
        verify(crewJoinRequestRepository, atLeastOnce()).save(any(CrewJoinRequest.class));
    }

    @Test
    public void When_SendRequest_Belonging_User_To_Crew_Expect_BelongToSomeCrewException() {
        final String email = "Lambda";
        Crew crew = textureFactory.makeCrew(false);
        User user = textureFactory.makeUser(email,false);
        CrewUser crewUser = textureFactory.makeCrewUser(crew, user,false);

        //설정된 크루o ,Request 보낸적 없는 유저.
        when(crewJoinRequestRepository.findCrewJoinRequestByCrewAndUser(any(Crew.class), any(User.class))).thenReturn(Optional.empty());
        when(crewUserRepository.findByCrewAndUser(any(Crew.class), any(User.class))).thenReturn(Optional.of(crewUser));

        assertThrows(BelongToSomeCrewException.class, () -> {
            crewJoinRequestService.sendJoinRequest(crew, user);
        });
    }

    @Test
    void When_CancelRequest_Expect_Call_delete(){
        final String email = "Lambda";
        Crew crew = textureFactory.makeCrew(false);
        User user = textureFactory.makeUser(email,false);

        CrewJoinRequest request= textureFactory.makeRequest(crew,user,false);

        when(crewJoinRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        //then
        crewJoinRequestService.cancelJoinRequest(request);
        verify(crewJoinRequestRepository,atLeastOnce()).delete(any(CrewJoinRequest.class));
    }

    @Test
    void When_acknowledgeRequest_Expect_Call_crewJoinRequest_Delete_crewUser_Save(){
        final String email="Lambda";
        Crew crew=textureFactory.makeCrew(false);
        User user=textureFactory.makeUser(email,false);

        CrewJoinRequest request=textureFactory.makeRequest(crew,user,false);

        crewJoinRequestService.acknowledgeJoinRequest(request);

        verify(crewJoinRequestRepository,atLeastOnce()).delete(any(CrewJoinRequest.class));
        verify(crewUserRepository,atLeastOnce()).save(any(CrewUser.class));
    }

    Crew makeCrewWithGradeLimit(Grade grade) {
        return Crew.builder()
                .gradeLimit(grade)
                .name("Lambda Crew!")
                .build();
    }

    User makeUserWithGrade(Grade grade) {
        User user = new User();
        user.setUsername("Lambda");
        user.setEmail("Lambda@Lambda.com");
        user.setGrade(grade);
        return user;
    }
}
