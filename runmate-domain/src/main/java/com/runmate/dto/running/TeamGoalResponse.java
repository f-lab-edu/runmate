package com.runmate.dto.running;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class TeamGoalResponse {
    private final float distance;
    private final long runningTime;
    private final LocalTime pace;
}
