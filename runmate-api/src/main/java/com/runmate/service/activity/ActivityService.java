package com.runmate.service.activity;

import com.runmate.domain.activity.Activities;
import com.runmate.domain.activity.Activity;
import com.runmate.domain.user.User;
import com.runmate.dto.activity.ActivityDto;
import com.runmate.dto.activity.ActivityStatisticsDto;
import com.runmate.exception.NotFoundUserEmailException;
import com.runmate.repository.activity.ActivityQueryRepository;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityQueryRepository activityQueryRepository;
    private final UserRepository userRepository;

    public void completeActivity(String email, Activity activity) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserEmailException::new);
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
