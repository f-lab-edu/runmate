package com.runmate.domain.activity;

import lombok.Getter;

import java.time.LocalTime;
import java.util.Objects;

@Getter
public class RunningTime {

    private final int hour;

    private final int minute;

    private final int second;

    private RunningTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public static RunningTime from(LocalTime time) {
        return new RunningTime(time.getHour(), time.getMinute(), time.getSecond());
    }

    public static RunningTime of(int hour, int minute, int second) {
        return new RunningTime(hour, minute, second);
    }

    public static RunningTime add(RunningTime left, RunningTime right) {
        int seconds = left.second + right.second;
        int minutes = left.minute + right.minute + seconds / 60;
        int hours = left.hour + right.hour + minutes / 60;

        seconds %= 60;
        minutes %= 60;

        return new RunningTime(hours, minutes, seconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunningTime that = (RunningTime) o;
        return hour == that.hour && minute == that.minute && second == that.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute, second);
    }
}
