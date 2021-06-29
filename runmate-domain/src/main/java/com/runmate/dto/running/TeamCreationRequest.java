package com.runmate.dto.running;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime startTime;
}
