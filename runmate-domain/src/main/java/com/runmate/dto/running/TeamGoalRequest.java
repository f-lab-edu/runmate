package com.runmate.dto.running;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Positive;

@Getter
@RequiredArgsConstructor
public class TeamGoalRequest {
    @Positive
    private final float distance;
    @Positive
    private final long runningTime;
}
