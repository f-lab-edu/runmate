package com.runmate.service.activity;

import com.runmate.domain.activity.Activities;
import com.runmate.domain.activity.Activity;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

        if (user.canUpgrade(activities.calTotalDistance())) {
            user.upgrade();
            userRepository.save(user);
        }
    }
}
