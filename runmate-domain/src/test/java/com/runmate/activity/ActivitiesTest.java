package com.runmate.activity;

import com.runmate.domain.activity.Activities;
import com.runmate.domain.activity.Activity;
import com.runmate.domain.activity.RunningTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ActivitiesTest {

    private Activities activities;
    private Activities emptyActivities;

    @BeforeEach
    void setUp() {
        Activity activity1 = Activity.builder()
                .distance(1.05f)
                .calories(104)
                .runningTime(LocalTime.of(0, 8, 45))
                .build();

        Activity activity2 = Activity.builder()
                .distance(5.40f)
                .calories(380)
                .runningTime(LocalTime.of(0, 28, 30))
                .build();

        Activity activity3 = Activity.builder()
                .distance(11.04f)
                .calories(704)
                .runningTime(LocalTime.of(0, 54, 20))
                .build();

        activities = new Activities(Arrays.asList(activity1, activity2, activity3));
        emptyActivities = new Activities(Collections.emptyList());
    }

    @DisplayName("세 개의 활동이 포함된 Activities 총 거리 계산")
    @Test
    void calculateTotalDistance() {
        //when
        float totalDistance = activities.calculateTotalDistance();

        //then
        assertThat(totalDistance).isEqualTo(17.49f);
    }

    @DisplayName("어떠한 활동도 없는 Activities 총 거리 계산")
    @Test
    void givenEmptyActivities_WhenCalculateTotalDistance_ThenReturn0() {
        //when
        float totalDistance = emptyActivities.calculateTotalDistance();

        //then
        assertThat(totalDistance).isEqualTo(0f);
    }

    @DisplayName("세 개의 활동이 포함된 Activities 총 러닝 시간 계산")
    @Test
    void calculateTotalRunningTime() {
        //when
        RunningTime totalRunningTime = activities.calculateTotalRunningTime();

        //then
        assertThat(totalRunningTime).isEqualTo(RunningTime.of(1, 31, 35));
    }

    @DisplayName("어떠한 활동도 없는 Activities 총 러닝 시간 계산")
    @Test
    void givenEmptyActivities_WhenCalculateTotalRunningTime_ThenReturn0() {
        //when
        RunningTime totalRunningTime = emptyActivities.calculateTotalRunningTime();

        //then
        assertThat(totalRunningTime).isEqualTo(RunningTime.of(0, 0, 0));
    }

    @DisplayName("세 개의 활동이 포함된 Activities 총 소모 칼로리 계산")
    @Test
    void calculateTotalCalories() {
        //when
        int totalCalories = activities.calculateTotalCalories();

        //then
        assertThat(totalCalories).isEqualTo(1188);
    }

    @DisplayName("어떠한 활동도 없는 Activities 총 소모 칼로리 계산")
    @Test
    void givenEmptyActivities_WhenCalculateTotalCalories_ThenReturn0() {
        //when
        int totalCalories = emptyActivities.calculateTotalCalories();

        //then
        assertThat(totalCalories).isEqualTo(0);
    }
}