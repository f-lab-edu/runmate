package com.runmate.service;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.domain.activity.Activity;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityQueryRepository;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.activity.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@SpringBootTest
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
public class ActivityServiceTest {
    private static final int CURRENT_YEAR = LocalDateTime.now().getYear();
    private static final Month CURRENT_MONTH = LocalDateTime.now().getMonth();

    @MockBean
    UserRepository userRepository;
    @MockBean
    ActivityRepository activityRepository;

    @MockBean
    ActivityQueryRepository activityQueryRepository;

    @Autowired
    ActivityService service;

    User user;
    Activity completedActivity;
    List<Activity> activities;

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

        user = User.ofGrade()
                .grade(Grade.UNRANKED)
                .email("ppp@ppp.com")
                .build();
        user.setActivities(activities);

        Activity activity3 = Activity.builder()
                .distance(10.5f)
                .runningTime(LocalTime.of(0, 50, 45))
                .calories(500)
                .createdAt(LocalDateTime.now())
                .build();

        Activity activity4 = Activity.builder()
                .distance(8.2f)
                .runningTime(LocalTime.of(0, 38, 52))
                .calories(418)
                .createdAt(LocalDateTime.now().plusSeconds(10L))
                .build();

        this.activities = Arrays.asList(activity3, activity4);
    }

    @Test
    public void When_CompleteActivity_Expect_Upgrade() {
        //given
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

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
                .thenReturn(Optional.of(user));

        completedActivity = Activity.builder().distance(4).build();

        //when
        service.completeActivity(user.getEmail(), completedActivity);

        //then
        Mockito.verify(userRepository, never()).save(any(User.class));
    }
}
