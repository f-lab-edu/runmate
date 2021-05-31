package com.runmate.service.activity;

import com.runmate.domain.activity.Activities;
import com.runmate.domain.activity.Activity;
import com.runmate.dto.activity.ActivityDto;
import com.runmate.dto.activity.ActivityStatisticsDto;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public void completeActivity(String email, Activity activity) {
        User user = userRepository.findByEmail(email);
        user.completeActivity(activity);

        activityRepository.save(activity);

        Activities activities = new Activities(user.getActivities());

        if (user.canUpgrade(activities.calculateTotalDistance())) {
            user.upgrade();
            userRepository.save(user);
        }
    }

    @Transactional(readOnly = true)
    public ActivityStatisticsDto findYearlyStatistics(String email, int year) {
        LocalDate fromDate = LocalDate.of(year, 1, 1);
        LocalDate toDate = LocalDate.of(year, 12, 31);

        return findStatisticsDuringPeriod(email, fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public ActivityStatisticsDto findMonthlyStatistics(String email, int year, Month month) {
        LocalDate fromDate = LocalDate.of(year, month, 1);
        LocalDate toDate = LocalDate.of(year, month, month.length(Year.isLeap(year)));

        return findStatisticsDuringPeriod(email, fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public ActivityStatisticsDto findStatisticsDuringPeriod(String email, LocalDate fromDate, LocalDate toDate) {
        User user = userRepository.findByEmail(email);

        LocalDateTime from = LocalDateTime.of(fromDate, LocalTime.MIN);
        LocalDateTime to = LocalDateTime.of(toDate, LocalTime.MAX);

        Activities activities = new Activities(activityRepository.findAllByUserAndBetweenDates(user.getId(), from, to));

        return activities.toStatistics();
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> findActivitiesWithPagination(String email, int offset, int limit) {
        User user = userRepository.findByEmail(email);

        List<Activity> activities = activityRepository.findAllByUserWithPagination(user.getId(),
                PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "createdAt")));

        return activities.stream()
                .map(ActivityDto::of)
                .collect(Collectors.toList());
    }
}
