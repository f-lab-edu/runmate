package com.runmate.domain.activity;

import java.util.List;

public class Activities {
    private List<Activity> activities;

    public Activities(List<Activity> activities) {
        this.activities = activities;
    }

    public float calTotalDistance() {
        float result = 0;
        for (Activity activity : activities) {
            result += activity.getDistance();
        }
        return result;
    }
}
