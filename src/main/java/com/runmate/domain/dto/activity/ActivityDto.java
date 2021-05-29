package com.runmate.domain.dto.activity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class ActivityDto {
    private final Long id;
    private final float distance;
    private final long runningTime;
    @JsonFormat(pattern = "hh:mm:ss")
    private final LocalTime averagePace;
    private final int calories;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private final LocalDateTime createdAt;

    public ActivityDto(long id, float distance, long runningTime, double averagePace, int calories, LocalDateTime createdAt) {
        this.id = id;
        this.distance = distance;
        this.runningTime = runningTime;
        this.averagePace = LocalTime.ofSecondOfDay((long) averagePace);
        this.calories = calories;
        this.createdAt = createdAt;
    }
}
