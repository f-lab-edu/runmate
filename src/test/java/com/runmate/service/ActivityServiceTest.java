package com.runmate.service;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.activity.RunningTime;
import com.runmate.domain.dto.ActivityDto;
import com.runmate.domain.dto.ActivityStatisticsDto;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.activity.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@SpringBootTest
public class ActivityServiceTest {
    private static final int CURRENT_YEAR = LocalDateTime.now().getYear();
    private static final Month CURRENT_MONTH = LocalDateTime.now().getMonth();

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

    @DisplayName("연도별 활동 통계 조회")
    @Test
    public void findYearlyStatistics() {
        //when
        ActivityStatisticsDto yearlyStatistics = service.findYearlyStatistics(user.getEmail(), CURRENT_YEAR);

        //then
        assertThat(yearlyStatistics.getCount()).isEqualTo(2);
        assertThat(yearlyStatistics.getDistance()).isEqualTo(18.7f);
        assertThat(yearlyStatistics.getRunningTime()).isEqualTo(RunningTime.of(1, 29, 37));
        assertThat(yearlyStatistics.getPace()).isEqualTo(LocalTime.of(0, 4, 48));
        assertThat(yearlyStatistics.getCalories()).isEqualTo(918);
    }

    @DisplayName("어떤 활동도 없는 연도의 연도별 통계 조회")
    @Test
    public void findYearlyStatisticsWhenHasNotAnyActivities() {
        //when
        ActivityStatisticsDto yearlyStatistics = service.findYearlyStatistics(user.getEmail(), 1960);

        //then
        assertThat(yearlyStatistics.getCount()).isEqualTo(0);
        assertThat(yearlyStatistics.getDistance()).isEqualTo(0f);
        assertThat(yearlyStatistics.getRunningTime()).isEqualTo(LocalTime.of(0, 0, 0));
        assertThat(yearlyStatistics.getPace()).isEqualTo(LocalTime.of(0, 0, 0));
        assertThat(yearlyStatistics.getCalories()).isEqualTo(0);
    }

    @DisplayName("월별 활동 통계 조회")
    @Test
    public void findMonthlyStatistics() {
        //when
        ActivityStatisticsDto monthlyStatistics =
                service.findMonthlyStatistics(user.getEmail(), CURRENT_YEAR, CURRENT_MONTH);

        //then
        assertThat(monthlyStatistics.getCount()).isEqualTo(2);
        assertThat(monthlyStatistics.getDistance()).isEqualTo(18.7f);
        assertThat(monthlyStatistics.getRunningTime()).isEqualTo(RunningTime.of(1, 29, 37));
        assertThat(monthlyStatistics.getPace()).isEqualTo(LocalTime.of(0, 4, 48));
        assertThat(monthlyStatistics.getCalories()).isEqualTo(918);
    }

    @DisplayName("주별 활동 통계 조회")
    @Test
    public void findWeeklyStatistics() {
        //given
        User newUser = new User();

        LocalDate from = LocalDate.of(2021, 4, 25);
        LocalDate to = LocalDate.of(2021, 5, 1);

        //when
        ActivityStatisticsDto weeklyStatistics = service.findWeeklyStatistics(newUser.getEmail(), from, to);

        //then
        assertThat(weeklyStatistics.getCount()).isEqualTo(2);
        assertThat(weeklyStatistics.getDistance()).isEqualTo(18.7f);
        assertThat(weeklyStatistics.getRunningTime()).isEqualTo(RunningTime.of(1, 29, 37));
        assertThat(weeklyStatistics.getPace()).isEqualTo(LocalTime.of(0, 4, 48));
        assertThat(weeklyStatistics.getCalories()).isEqualTo(918);
    }

}
