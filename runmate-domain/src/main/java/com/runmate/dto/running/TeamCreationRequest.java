package com.runmate.dto.running;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamCreationRequest {
    @NotNull
    private final long leaderId;
    @NotBlank
    private final String title;
    private final List<String> emails;
    @NotNull
    private final TeamGoalRequest goal;
    private final LocalDateTime startTime;
}
