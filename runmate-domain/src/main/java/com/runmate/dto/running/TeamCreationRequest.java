package com.runmate.dto.running;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamCreationRequest {
    private final long leaderId;
    private final String title;
    private final List<String> emails;
    private final TeamGoalRequest goal;
    private final LocalDateTime startTime;
}
