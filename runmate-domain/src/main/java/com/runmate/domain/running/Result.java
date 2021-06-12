package com.runmate.domain.running;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Result {
    @Column(name = "result_total_distance")
    private float totalDistance;

    @Column(name = "result_total_running_seconds")
    private long totalRunningSeconds;

    @Column(name = "complete_status")
    @Enumerated(EnumType.STRING)
    private CompleteStatus completeStatus;

    @Builder
    public Result(float totalDistance, long totalRunningSeconds, CompleteStatus status) {
        this.totalDistance = totalDistance;
        this.totalRunningSeconds = totalRunningSeconds;
        this.completeStatus = status;
    }
}
