package com.runmate.service.activity;

import com.runmate.domain.activity.Activities;
import com.runmate.domain.activity.Activity;
import com.runmate.domain.dto.activity.ActivityDto;
import com.runmate.domain.dto.activity.ActivityStatisticsDto;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityQueryRepository;
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
    private final ActivityQueryRepository activityQueryRepository;
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
    public ActivityStatisticsDto findStatisticsDuringPeriod(String email, LocalDate fromDate, LocalDate toDate) {
        return activityQueryRepository.findAllByUserAndBetweenDates(email, fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> findActivitiesWithPagination(String email, int offset, int limit) {
        return activityQueryRepository.findAllByUserWithPagination(email, PageRequest.of(offset, limit));
    }
}
