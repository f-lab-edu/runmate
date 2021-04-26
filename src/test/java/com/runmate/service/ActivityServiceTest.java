package com.runmate.service;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.activity.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@SpringBootTest
public class ActivityServiceTest {
    @MockBean
    UserRepository userRepository;
    @MockBean
    ActivityRepository activityRepository;

    @Autowired
    ActivityService service;

    User user;
    Activity completedActivity;
    @BeforeEach
    public void setUp(){
        Activity activity1=new Activity();
        activity1.setDistance(15);

        Activity activity2=new Activity();
        activity2.setDistance(30);

        user=new User();
        user.setGrade(Grade.UNRANKED);
        user.setEmail("ppp@ppp.com");
        user.setActivities(Arrays.asList(activity1,activity2));
    }

    @Test
    public void When_CompleteActivity_Expect_Upgrade(){
        //given
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(user);
        completedActivity=new Activity();
        completedActivity.setDistance(5);

        //when
        service.completeActivity(user.getEmail(),completedActivity);

        //then
        Mockito.verify(userRepository).save(any(User.class));
    }
    @Test
    public void When_CompleteActivity_Expect_DoNotUpgrade(){
        //given
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(user);
        completedActivity=new Activity();
        completedActivity.setDistance(4);

        //when
        service.completeActivity(user.getEmail(),completedActivity);

        //then
        Mockito.verify(userRepository,never()).save(any(User.class));
    }
}
