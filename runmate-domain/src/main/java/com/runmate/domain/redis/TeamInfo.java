package com.runmate.domain.redis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.runmate.domain.running.Goal;
import com.runmate.exception.AdminNotIncludedException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamInfo {
    @Id
    private long teamId;
    private List<Long> members = new ArrayList<>();
    private float totalDistance;
    private long adminId;
    private GoalForTempStore goal;
    private long runningSeconds;

    @Builder(builderMethodName = "builder")
    public TeamInfo(long teamId, long adminId, GoalForTempStore goal) {
        this.teamId = teamId;
        this.goal = goal;

        this.adminId = adminId;

        this.totalDistance = 0;
        this.runningSeconds = 0;
    }

    @Builder(builderMethodName = "fromGoal", builderClassName = "forMove")
    public TeamInfo(long teamId, long adminId, Goal goal) {
        this.teamId = teamId;
        this.adminId = adminId;
        this.totalDistance = 0;
        this.runningSeconds = 0;

        this.goal = GoalForTempStore.builder()
                .startedAt(goal.getStartedAt())
                .distance(goal.getTotalDistance())
                .runningSeconds(goal.getTotalRunningSeconds())
                .build();
    }

    public float increaseTotalDistance(float distance) {
        this.totalDistance += distance;
        updateRunningSeconds();
        return this.totalDistance;
    }

    private void updateRunningSeconds() {
        this.runningSeconds = Duration.between(this.goal.getStartedAt(), now()).getSeconds();
    }

    @JsonIgnore
    public boolean isSuccessOnRunning() {
        return !isTimeOver() && totalDistance >= goal.getDistance();
    }

    @JsonIgnore
    public boolean isTimeOver() {
        return goal.getRunningSeconds() < this.runningSeconds;
    }

    @JsonIgnore
    public boolean isFailOnRunning() {
        return isTimeOver() && totalDistance < goal.getDistance();
    }
}