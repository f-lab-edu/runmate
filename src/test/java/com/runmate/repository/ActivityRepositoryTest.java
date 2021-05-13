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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertEquals(activityRepository.findAll().size(), 11);
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

    @DisplayName("최근 활동 기록 조회: 최근순으로 페이지당 5개 기록, 3개 반환")
    @Test
    void findActivitiesWithPagination() {
        //given
        String email = "sung@sung.com";
        User user = userRepository.findByEmail(email);

        int offset = 0;
        int limit = 5;
        Sort sortByCreatedAt = Sort.by(Sort.Direction.DESC, "createdAt");

        //when
        List<Activity> activities = activityRepository.findAllByUserWithPagination(user.getId(), PageRequest.of(offset, limit, sortByCreatedAt));

        //then
        assertNotNull(activities);
        assertThat(activities.size()).isEqualTo(3);

        assertThat(activities.get(0).getDistance()).isEqualTo(128f);
        assertThat(activities.get(0).getRunningTime()).isEqualTo(LocalTime.of(11, 47, 30));
        assertThat(activities.get(0).getCalories()).isEqualTo(10555);

        assertThat(activities.get(1).getDistance()).isEqualTo(120.195f);
        assertThat(activities.get(1).getRunningTime()).isEqualTo(LocalTime.of(12, 15, 30));
        assertThat(activities.get(1).getCalories()).isEqualTo(10526);

        assertThat(activities.get(2).getDistance()).isEqualTo(80.195f);
        assertThat(activities.get(2).getRunningTime()).isEqualTo(LocalTime.of(8, 15, 30));
        assertThat(activities.get(2).getCalories()).isEqualTo(9024);
    }

    @DisplayName("최근 활동 기록 조회: 결과가 없는 페이지 요청시 빈 리스트 반환")
    @Test
    void givenNoResultForPage_WhenFindActivitiesWithPagination_ThenReturnEmptyList() {
        //given
        String email = "sung@sung.com";
        User user = userRepository.findByEmail(email);

        int offset = 3;
        int limit = 5;
        Sort sortByCreatedAt = Sort.by(Sort.Direction.DESC, "createdAt");

        //when
        List<Activity> activities = activityRepository.findAllByUserWithPagination(user.getId(), PageRequest.of(offset, limit, sortByCreatedAt));

        //then
        assertNotNull(activities);
        assertThat(activities.size()).isEqualTo(0);
    }
}
