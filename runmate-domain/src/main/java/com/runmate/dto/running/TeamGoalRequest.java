package com.runmate.dto.running;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamGoalRequest {
    private final float distance;
    private final long runningTime;
}
