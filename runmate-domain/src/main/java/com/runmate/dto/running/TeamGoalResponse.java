package com.runmate.dto.running;

import com.runmate.domain.running.Goal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class TeamGoalResponse {
    private final float distance;
    private final long runningTime;
    private final LocalTime pace;

    public static TeamGoalResponse from(Goal goal) {
        return new TeamGoalResponse(goal.getTotalDistance(), goal.getTotalRunningSeconds(), goal.calculatePace());
    }
}
