package com.runmate.domain.dto;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.activity.RunningTime;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class ActivityDto {

    private final Long id;

    private final float distance;

    private final RunningTime runningTime;

    private final LocalTime averagePace;

    private final int calories;

    private final LocalDateTime createdAt;

    public static ActivityDto of(Activity source) {
        return new ActivityDto(source.getId(), source.getDistance(), RunningTime.from(source.getRunningTime()),
                source.calculatePace(), source.getCalories(), source.getCreatedAt());
    }

    private ActivityDto(Long id, float distance, RunningTime runningTime, LocalTime averagePace, int calories, LocalDateTime createdAt) {
        this.id = id;
        this.distance = distance;
        this.runningTime = runningTime;
        this.averagePace = averagePace;
        this.calories = calories;
        this.createdAt = createdAt;
    }
}
