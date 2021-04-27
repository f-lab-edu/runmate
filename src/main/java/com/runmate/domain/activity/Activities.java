package com.runmate.domain.activity;

import com.runmate.domain.dto.ActivityStatisticsDto;
import com.runmate.utils.TimeUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Activities {

    private final List<Activity> activities;

    public Activities(List<Activity> activities) {
        this.activities = new ArrayList<>(activities);
    }

    public float calculateTotalDistance() {
        return activities.stream()
                .map(Activity::getDistance)
                .reduce(Float::sum)
                .orElse(0f);
    }

    public RunningTime calculateTotalRunningTime() {
        return activities.stream()
                .map(Activity::getRunningTime)
                .map(RunningTime::from)
                .reduce(RunningTime::add)
                .orElse(RunningTime.of(0, 0, 0));
    }

    public int calculateTotalCalories() {
        return activities.stream()
                .map(Activity::getCalories)
                .reduce(Integer::sum)
                .orElse(0);
    }

    public ActivityStatisticsDto toStatistics() {
        float totalDistance = calculateTotalDistance();
        RunningTime totalRunningTime = calculateTotalRunningTime();
        int totalCalories = calculateTotalCalories();

        long totalSeconds = TimeUtils.runningTimeToSeconds(totalRunningTime);
        long secondsPerKilometer = (long) (totalSeconds / totalDistance);
        LocalTime averagePace = TimeUtils.secondsToTime(secondsPerKilometer);

        return ActivityStatisticsDto.of(activities.size(), totalDistance, totalRunningTime, averagePace, totalCalories);
    }
}
