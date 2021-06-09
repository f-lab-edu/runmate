package com.runmate.dto.activity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class ActivityStatisticsDto {
    private final long count;
    private final float distance;
    private final long runningTime;
    @JsonFormat(pattern = "hh:mm:ss")
    private final LocalTime averagePace;
    private final int calories;

    public ActivityStatisticsDto(long count, float distance, long runningTime, double averagePace, int calories) {
        this.count = count;
        this.distance = distance;
        this.runningTime = runningTime;
        this.averagePace = LocalTime.ofSecondOfDay((long) averagePace);
        this.calories = calories;
    }
}
