package com.runmate.domain;

import com.runmate.domain.redis.GoalForTempStore;
import com.runmate.domain.redis.TeamInfo;
import com.runmate.exception.AdminNotIncludedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TeamInfoTest {
    List<Long> memberIds = Arrays.asList(2L, 3L);
    TeamInfo teamInfo;
    GoalForTempStore goal;

    @BeforeEach
    void setUp() {
        goal = GoalForTempStore.builder()
                .startedAt(LocalDateTime.now())
                .distance(10)
                .runningSeconds(16000)
                .build();
        teamInfo = TeamInfo.builder()
                .teamId(2L)
                .adminId(3L)
                .goal(goal)
                .build();
    }

    @Test
    void When_IncreaseTeamInfoDistance_Expect_IncreasedDistance() {
        final float distance = 10.5F;
        final float beforeIncrease = teamInfo.getTotalDistance();
        teamInfo.increaseTotalDistance(distance);

        assertEquals(teamInfo.getTotalDistance(), beforeIncrease + distance);
    }

    @Test
    void When_IsSuccessOnRunning_Expect_True() {
        final float goalDistance = 10F;
        final float currentDistance = 12F;
        final LocalDateTime start = LocalDateTime.now().minus(1,ChronoUnit.HOURS);
        final long hour = 3600;

        goal = GoalForTempStore.builder()
                .startedAt(start)
                .distance(goalDistance)
                .runningSeconds(hour)
                .build();

        teamInfo = TeamInfo.builder()
                .teamId(2L)
                .adminId(3L)
                .goal(goal)
                .build();
        teamInfo.increaseTotalDistance(currentDistance);
        teamInfo.isSuccessOnRunning();
    }

    @Test
    void When_IsFailOnRunning_Expect_True() {
        final float goalDistance = 10F;
        final float currentDistance = 8F;
        final LocalDateTime start = LocalDateTime.now().plus(3600, ChronoUnit.SECONDS);

        goal = GoalForTempStore.builder()
                .startedAt(start)
                .distance(goalDistance)
                .runningSeconds(10)
                .build();

        teamInfo = TeamInfo.builder()
                .teamId(2L)
                .adminId(3L)
                .goal(goal)
                .build();
        teamInfo.increaseTotalDistance(currentDistance);
        teamInfo.isFailOnRunning();
    }

    @Test
    void When_IsTimeOver_Expect_True() {
        goal = GoalForTempStore.builder()
                .startedAt(LocalDateTime.now().minus(10, ChronoUnit.HOURS))
                .distance(10)
                .runningSeconds(1)
                .build();
        teamInfo = TeamInfo.builder()
                .teamId(2L)
                .adminId(3L)
                .goal(goal)
                .build();

        teamInfo.isTimeOver();
    }
}
