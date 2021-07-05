package com.runmate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.runmate.domain.running.Position;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningMessage {
    private Long teamId;
    private Long memberId;
    private String username;
    private LocalTime averagePace;
    private LocalTime instantaneousPace;
    @JsonProperty("position")
    private Position position;
    private float distance;
    private MessageType messageType;

    @Builder
    public RunningMessage(Long teamId, Long memberId, String username, LocalTime averagePace, LocalTime instantaneousPace, Position position, float distance) {
        this.teamId = teamId;
        this.memberId = memberId;
        this.username = username;
        this.averagePace = averagePace;
        this.instantaneousPace = instantaneousPace;
        this.position = position;
        this.distance = distance;
        this.messageType = MessageType.RUNNING;
    }

    public void changeMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
