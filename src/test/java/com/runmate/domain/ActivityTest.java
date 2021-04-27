package com.runmate.domain;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ActivityTest {
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
}
