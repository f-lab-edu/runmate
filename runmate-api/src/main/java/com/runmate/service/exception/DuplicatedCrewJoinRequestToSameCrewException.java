package com.runmate.service.exception;

public class DuplicatedCrewJoinRequestToSameCrewException extends InvalidValueException{
    public DuplicatedCrewJoinRequestToSameCrewException(String message) {
        super(message);
    }
}
