package com.runmate.websocket.domain;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class AlertMessage {

    private final String username;
    private final LocalTime paceDifference;
    private final LocalTime averagePace;
    private final Position position;

    private AlertMessage(String username, LocalTime paceDifference, LocalTime averagePace, Position position) {
        this.username = username;
        this.paceDifference = paceDifference;
        this.averagePace = averagePace;
        this.position = position;
    }

    public static AlertMessage of(RunningMessage source, LocalTime target) {
        LocalTime averagePace = source.getAveragePace();
        LocalTime paceDifference = averagePace.minusNanos(target.toNanoOfDay());
        return new AlertMessage(source.getUsername(), paceDifference, averagePace, source.getPosition());
    }
}
