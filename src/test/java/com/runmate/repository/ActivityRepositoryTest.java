package com.runmate.repository;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ActivityRepositoryTest {
    public static final int CURRENT_YEAR = LocalDate.now().getYear();
    public static final Month CURRENT_MONTH = LocalDate.now().getMonth();
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;

    static final String ADDRESS = "you@you.com";
    User user;

    @BeforeEach
    public void setUp() {
        user = userRepository.findByEmail(ADDRESS);
    }

    @Test
    public void save() {
        Activity activity = Activity.builder()
                .distance(42.195F)
                .runningTime(LocalTime.of(7, 30))
                .calories(4503)
                .build();
        activity.setUser(user);

        activityRepository.save(activity);
        assertEquals(activityRepository.findAll().size(), 8);
    }

    @DisplayName("올해 동안의 활동 기록 검색")
    @Test
    void findActivitiesDuringCurrentYear() {
        //given
        LocalDate fromDate = LocalDate.of(CURRENT_YEAR, 1, 1);
        LocalDateTime from = LocalDateTime.of(fromDate, LocalTime.MIN);

        LocalDate toDate = LocalDate.of(CURRENT_YEAR, 12, 31);
        LocalDateTime to = LocalDateTime.of(toDate, LocalTime.MAX);

        //when
        List<Activity> activities = activityRepository.findAllByUserAndBetweenDates(user.getId(), from, to);

        //then
        assertThat(activities.get(0).getDistance()).isEqualTo(10.5f);
        assertThat(activities.get(0).getRunningTime()).isEqualTo(LocalTime.of(0, 50, 45));
        assertThat(activities.get(0).getCalories()).isEqualTo(500);

        assertThat(activities.get(1).getDistance()).isEqualTo(8.2f);
        assertThat(activities.get(1).getRunningTime()).isEqualTo(LocalTime.of(0, 38, 52));
        assertThat(activities.get(1).getCalories()).isEqualTo(418);
    }

    @DisplayName("이번달 동안의 활동 기록 검색")
    @Test
    void findActivitiesDuringCurrentMonth() {
        //given
        LocalDate fromDate = LocalDate.of(CURRENT_YEAR, CURRENT_MONTH, 1);
        LocalDateTime from = LocalDateTime.of(fromDate, LocalTime.MIN);

        LocalDate toDate = LocalDate.of(CURRENT_YEAR, CURRENT_MONTH, CURRENT_MONTH.length(Year.isLeap(CURRENT_YEAR)));
        LocalDateTime to = LocalDateTime.of(toDate, LocalTime.MAX);

        //when
        List<Activity> activities = activityRepository.findAllByUserAndBetweenDates(user.getId(), from, to);

        //then
        assertThat(activities.get(0).getDistance()).isEqualTo(10.5f);
        assertThat(activities.get(0).getRunningTime()).isEqualTo(LocalTime.of(0, 50, 45));
        assertThat(activities.get(0).getCalories()).isEqualTo(500);

        assertThat(activities.get(1).getDistance()).isEqualTo(8.2f);
        assertThat(activities.get(1).getRunningTime()).isEqualTo(LocalTime.of(0, 38, 52));
        assertThat(activities.get(1).getCalories()).isEqualTo(418);
    }
}
