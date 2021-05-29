package com.runmate.dto.activity;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.activity.RunningTime;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class ActivityStatisticsDto {

    private final int count;

    private final float distance;

    private final RunningTime runningTime;

    private final LocalTime averagePace;

    private final int calories;

    public static ActivityStatisticsDto from(Activity source) {
        return new ActivityStatisticsDto(1, source.getDistance(), RunningTime.from(source.getRunningTime())
                                        , source.calculatePace(), source.getCalories());
    }

    public static ActivityStatisticsDto of (int count, float distance, RunningTime runningTime, LocalTime pace, int calories) {
        return new ActivityStatisticsDto(count, distance, runningTime, pace, calories);
    }

    private ActivityStatisticsDto(int count, float distance, RunningTime runningTime, LocalTime averagePace, int calories) {
        this.count = count;
        this.distance = distance;
        this.runningTime = runningTime;
        this.averagePace = averagePace;
        this.calories = calories;
    }
}
