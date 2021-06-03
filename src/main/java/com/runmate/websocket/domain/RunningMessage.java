package com.runmate.websocket.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningMessage implements Serializable {
    private Long teamId;
    private String username;
    private LocalTime averagePace;
    private LocalTime instantaneousPace;
    private Position position;

    public RunningMessage(Long teamId, String username, LocalTime averagePace, LocalTime instantaneousPace, Position position) {
        this.teamId = teamId;
        this.username = username;
        this.averagePace = averagePace;
        this.instantaneousPace = instantaneousPace;
        this.position = position;
    }
}