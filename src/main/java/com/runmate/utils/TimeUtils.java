package com.runmate.utils;

import com.runmate.domain.activity.RunningTime;

import java.time.LocalTime;

public class TimeUtils {
    private static final int HOUR_SECOND_UNIT = 3600;
    private static final int MINUTE_SECOND_UNIT = 60;

    public static long timeToSeconds(LocalTime time) {
        return time.getHour() * HOUR_SECOND_UNIT + time.getMinute() * MINUTE_SECOND_UNIT + time.getSecond();
    }

    public static LocalTime secondsToTime(long seconds) {
        int hour = (int) (seconds / HOUR_SECOND_UNIT);
        int minute = (int) ((seconds % HOUR_SECOND_UNIT) / MINUTE_SECOND_UNIT);
        int second = (int) ((seconds % HOUR_SECOND_UNIT) % MINUTE_SECOND_UNIT);

        return LocalTime.of(hour, minute, second);
    }

    public static long runningTimeToSeconds(RunningTime time) {
        return (long) time.getHour() * HOUR_SECOND_UNIT + (long) time.getMinute() * MINUTE_SECOND_UNIT + time.getSecond();
    }
}
