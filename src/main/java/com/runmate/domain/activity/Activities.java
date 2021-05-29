package com.runmate.domain.activity;

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
}
