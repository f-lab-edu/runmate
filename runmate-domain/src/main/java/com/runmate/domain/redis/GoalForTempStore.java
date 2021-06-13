package com.runmate.domain.redis;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.LocalDateTime.now;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class GoalForTempStore {
    private long runningSeconds;
    private float distance;
    private LocalDateTime startedAt;

    @Builder
    public GoalForTempStore(long runningSeconds, float distance, LocalDateTime startedAt) {
        this.runningSeconds = runningSeconds;
        this.distance = distance;
        this.startedAt = startedAt;
    }

    public boolean isGoalSuccess(float runningDistance) {
        LocalDateTime current = now();
        LocalDateTime endTime = startedAt.plus(getRunningSeconds(), ChronoUnit.SECONDS);
        if (current.isEqual(endTime) || current.isAfter(endTime)) {
            return runningDistance >= getDistance();
        }
        return false;
    }
}
