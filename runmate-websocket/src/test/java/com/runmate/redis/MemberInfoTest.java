package com.runmate.redis;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberInfoTest {
    @Test
    void When_IncreaseMemberInfoDistance_Expect_IncreasedDistance() {
        final float distance = 10.5F;
        GoalForTempStore goal = GoalForTempStore.builder()
                .startedAt(LocalDateTime.now())
                .distance(10)
                .runningSeconds(16000)
                .build();
        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(2L)
                .adminId(3L)
                .goal(goal)
                .build();

        MemberInfo info = MemberInfo.builder()
                .teamId(2L)
                .memberId(1L)
                .build();

        final float beforeIncrease = info.getTotalDistance();
        info.increaseTotalDistance(distance);

        assertEquals(beforeIncrease + distance, info.getTotalDistance());
    }
}
