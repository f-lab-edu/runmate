package com.runmate.domain.running;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningMessage {
    private Long teamId;
    private String username;
    private LocalTime averagePace;
    private LocalTime instantaneousPace;
    @JsonProperty("position")
    private Position position;

    public RunningMessage(Long teamId, String username, LocalTime averagePace, LocalTime instantaneousPace, Position position) {
        this.teamId = teamId;
        this.username = username;
        this.averagePace = averagePace;
        this.instantaneousPace = instantaneousPace;
        this.position = position;
    }
}