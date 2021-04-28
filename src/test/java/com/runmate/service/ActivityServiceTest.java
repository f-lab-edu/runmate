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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

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

        user = new User();
        user.setGrade(Grade.UNRANKED);
        user.setEmail("ppp@ppp.com");
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
        //given
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(user);

        LocalDate fromDate = LocalDate.of(CURRENT_YEAR, 1, 1);
        LocalDateTime from = LocalDateTime.of(fromDate, LocalTime.MIN);

        LocalDate toDate = LocalDate.of(CURRENT_YEAR, 12, 31);
        LocalDateTime to = LocalDateTime.of(toDate, LocalTime.MAX);

        Mockito.when(activityRepository.findAllByUserAndBetweenDates(user.getId(), from, to))
                .thenReturn(activities);

        //when
        ActivityStatisticsDto yearlyStatistics = service.findYearlyStatistics(user.getEmail(), CURRENT_YEAR);

        //then
        assertThat(yearlyStatistics.getCount()).isEqualTo(2);
        assertThat(yearlyStatistics.getDistance()).isEqualTo(18.7f);
        assertThat(yearlyStatistics.getRunningTime()).isEqualTo(RunningTime.of(1, 29, 37));
        assertThat(yearlyStatistics.getAveragePace()).isEqualTo(LocalTime.of(0, 4, 47));
        assertThat(yearlyStatistics.getCalories()).isEqualTo(918);
    }

    @DisplayName("어떤 활동도 없는 연도의 연도별 통계 조회")
    @Test
    public void findYearlyStatisticsWhenHasNotAnyActivities() {
        //given
        int year = 1960;
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(user);

        LocalDate fromDate = LocalDate.of(year, 1, 1);
        LocalDateTime from = LocalDateTime.of(fromDate, LocalTime.MIN);

        LocalDate toDate = LocalDate.of(year, 12, 31);
        LocalDateTime to = LocalDateTime.of(toDate, LocalTime.MAX);
        Mockito.when(activityRepository.findAllByUserAndBetweenDates(user.getId(), from, to))
                .thenReturn(Collections.emptyList());

        //when
        ActivityStatisticsDto yearlyStatistics = service.findYearlyStatistics(user.getEmail(), year);

        //then
        assertThat(yearlyStatistics.getCount()).isEqualTo(0);
        assertThat(yearlyStatistics.getDistance()).isEqualTo(0f);
        assertThat(yearlyStatistics.getRunningTime()).isEqualTo(RunningTime.from(LocalTime.of(0, 0, 0)));
        assertThat(yearlyStatistics.getAveragePace()).isEqualTo(LocalTime.of(0, 0, 0));
        assertThat(yearlyStatistics.getCalories()).isEqualTo(0);
    }

    @DisplayName("월별 활동 통계 조회")
    @Test
    public void findMonthlyStatistics() {
        //given
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(user);

        LocalDate fromDate = LocalDate.of(CURRENT_YEAR, CURRENT_MONTH, 1);
        LocalDateTime from = LocalDateTime.of(fromDate, LocalTime.MIN);

        LocalDate toDate = LocalDate.of(CURRENT_YEAR, CURRENT_MONTH, CURRENT_MONTH.length(Year.isLeap(CURRENT_YEAR)));
        LocalDateTime to = LocalDateTime.of(toDate, LocalTime.MAX);

        Mockito.when(activityRepository.findAllByUserAndBetweenDates(user.getId(), from, to))
                .thenReturn(activities);

        //when
        ActivityStatisticsDto monthlyStatistics =
                service.findMonthlyStatistics(user.getEmail(), CURRENT_YEAR, CURRENT_MONTH);

        //then
        assertThat(monthlyStatistics.getCount()).isEqualTo(2);
        assertThat(monthlyStatistics.getDistance()).isEqualTo(18.7f);
        assertThat(monthlyStatistics.getRunningTime()).isEqualTo(RunningTime.of(1, 29, 37));
        assertThat(monthlyStatistics.getAveragePace()).isEqualTo(LocalTime.of(0, 4, 47));
        assertThat(monthlyStatistics.getCalories()).isEqualTo(918);
    }

    @DisplayName("주별 활동 통계 조회")
    @Test
    public void findWeeklyStatistics() {
        //given
        LocalDate fromDate = LocalDate.of(2021, 4, 25);
        LocalDate toDate = LocalDate.of(2021, 5, 1);

        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(user);

        LocalDateTime from = LocalDateTime.of(fromDate, LocalTime.MIN);
        LocalDateTime to = LocalDateTime.of(toDate, LocalTime.MAX);

        Mockito.when(activityRepository.findAllByUserAndBetweenDates(user.getId(), from, to))
                .thenReturn(activities);

        //when
        ActivityStatisticsDto weeklyStatistics = service.findStatisticsDuringPeriod(user.getEmail(), fromDate, toDate);

        //then
        assertThat(weeklyStatistics.getCount()).isEqualTo(2);
        assertThat(weeklyStatistics.getDistance()).isEqualTo(18.7f);
        assertThat(weeklyStatistics.getRunningTime()).isEqualTo(RunningTime.of(1, 29, 37));
        assertThat(weeklyStatistics.getAveragePace()).isEqualTo(LocalTime.of(0, 4, 47));
        assertThat(weeklyStatistics.getCalories()).isEqualTo(918);
    }

    @DisplayName("최근 활동 조회 (2개의 활동이 조회된 경우)")
    @Test
    public void findRecentActivities() {
        //given
        int offset = 1;
        int limit = 5;

        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(user);

        List<Activity> sortedActivities = activities.stream()
                .sorted((o1, o2) -> -o1.getCreatedAt().compareTo(o2.getCreatedAt()))
                .collect(Collectors.toList());

        Mockito.when(activityRepository.findAllByUserWithPagination(user.getId(), PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "createdAt"))))
                .thenReturn(sortedActivities);

        //when
        List<ActivityDto> dtos = service.findActivitiesWithPagination(user.getEmail(), offset, limit);

        //then
        assertThat(dtos.size()).isEqualTo(2);

        assertThat(dtos.get(0).getDistance()).isEqualTo(8.2f);
        assertThat(dtos.get(0).getRunningTime()).isEqualTo(RunningTime.of(0, 38, 52));
        assertThat(dtos.get(0).getAveragePace()).isEqualTo(LocalTime.of(0, 4, 44));
        assertThat(dtos.get(0).getCalories()).isEqualTo(418);

        assertThat(dtos.get(1).getDistance()).isEqualTo(10.5f);
        assertThat(dtos.get(1).getRunningTime()).isEqualTo(RunningTime.of(0, 50, 45));
        assertThat(dtos.get(1).getAveragePace()).isEqualTo(LocalTime.of(0, 4, 50));
        assertThat(dtos.get(1).getCalories()).isEqualTo(500);
    }
}
