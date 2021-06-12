package com.runmate.domain.running;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

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
    @CreatedDate
    private LocalDateTime started_at;

    @Builder
    public Goal(float totalDistance, long totalRunningSeconds) {
        this.totalDistance = totalDistance;
        this.totalRunningSeconds = totalRunningSeconds;
    }
}
