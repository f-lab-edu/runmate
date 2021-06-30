package com.runmate.domain.running;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Goal {
    @Column(name = "goal_total_distance")
    private float totalDistance;

    @Column(name = "goal_running_seconds")
    private long totalRunningSeconds;

    @Column(name = "goal_started_at")
    private LocalDateTime startedAt;

    @Builder
    public Goal(float totalDistance, long totalRunningSeconds, LocalDateTime startedAt) {
        this.totalDistance = totalDistance;
        this.totalRunningSeconds = totalRunningSeconds;
        this.startedAt = startedAt;
    }

    public LocalTime calculatePace() {
        long secondsPerDistance = totalRunningSeconds / (long) totalDistance;
        return LocalDateTime.ofEpochSecond(secondsPerDistance, 0, ZoneOffset.UTC).toLocalTime();
    }
}
