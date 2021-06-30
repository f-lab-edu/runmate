package com.runmate.dto.running;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamCreationResponse {
    private final long id;
    private final String title;
    private final List<TeamMemberCreationResponse> members;
    private final TeamGoalResponse goal;
    private final LocalDateTime startTime;
}
