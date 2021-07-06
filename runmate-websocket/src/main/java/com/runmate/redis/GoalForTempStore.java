package com.runmate.redis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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

    @Builder
    public GoalForTempStore(long runningSeconds, float distance, LocalDateTime startedAt) {
        this.runningSeconds = runningSeconds;
        this.distance = distance;
        this.startedAt = startedAt;
    }

    public LocalDateTime calcEndTime() {
        return startedAt.plus(getRunningSeconds(), ChronoUnit.SECONDS);
    }
}
