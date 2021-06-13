package com.runmate.domain.running;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class IndividualResult {
    @Column(name = "individual_distance")
    private float totalDistance;

    @Column(name = "individual_running_seconds")
    private long totalRunningSeconds;

    @Builder
    public IndividualResult(float totalDistance, long totalRunningSeconds) {
        this.totalDistance = totalDistance;
        this.totalRunningSeconds = totalRunningSeconds;
    }
}
