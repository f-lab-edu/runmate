package com.runmate.domain.redis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.runmate.domain.running.Goal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class GoalForTempStore {
    private long runningSeconds;
    private float distance;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startedAt;

    @Builder(builderMethodName = "builder")
    public GoalForTempStore(long runningSeconds, float distance, LocalDateTime startedAt) {
        this.runningSeconds = runningSeconds;
        this.distance = distance;
        this.startedAt = startedAt;
    }

    @Builder(builderMethodName = "fromGoal", builderClassName = "forMove")
    public GoalForTempStore(Goal goal) {
        this.runningSeconds = goal.getTotalRunningSeconds();
        this.startedAt = goal.getStartedAt();
        this.distance = goal.getTotalDistance();
    }

    public LocalDateTime calcEndTime() {
        return startedAt.plus(getRunningSeconds(), ChronoUnit.SECONDS);
    }
}
