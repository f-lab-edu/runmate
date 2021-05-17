package com.runmate.service.exception;

public class DuplicatedCrewJoinRequestToSameCrewException extends RuntimeException{
    public DuplicatedCrewJoinRequestToSameCrewException(String message) {
        super(message);
    }
}
