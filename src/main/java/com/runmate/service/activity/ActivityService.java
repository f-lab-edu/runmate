package com.runmate.service.activity;

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

    public void completeActivity(String email, Activity activity){
        User user=userRepository.findByEmail(email);
        activity.setUser(user);

        float totalDistance=activity.getDistance();
        for(Activity pastActivity:user.getActivities()){
            totalDistance+=pastActivity.getDistance();
        }

        if(user.canUpgrade(totalDistance)){
            user.upgrade();
            userRepository.save(user);
        }
        activityRepository.save(activity);
    }
}
