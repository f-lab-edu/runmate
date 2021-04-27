package com.runmate.service;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.activity.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    public void setUp() {
        Activity activity1 = Activity.builder()
                .distance(15)
                .calories(123)
                .runningTime(LocalTime.of(02, 30, 30))
                .build();

        Activity activity2 = Activity.builder()
                .distance(30)
                .calories(123)
                .runningTime(LocalTime.of(02, 30, 30))
                .build();

        List<Activity> activities = new ArrayList<>();
        activities.add(activity1);
        activities.add(activity2);

        user = new User();
        user.setGrade(Grade.UNRANKED);
        user.setEmail("ppp@ppp.com");
        user.setActivities(activities);
    }

    @Test
    public void When_CompleteActivity_Expect_Upgrade() {
        //given
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(user);

        completedActivity = Activity.builder().distance(5).build();

        //when
        service.completeActivity(user.getEmail(), completedActivity);

        //then
        Mockito.verify(userRepository).save(any(User.class));
    }

    @Test
    public void When_CompleteActivity_Expect_DoNotUpgrade() {
        //given
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(user);

        completedActivity = Activity.builder().distance(4).build();

        //when
        service.completeActivity(user.getEmail(), completedActivity);

        //then
        Mockito.verify(userRepository, never()).save(any(User.class));
    }
}
