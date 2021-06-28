package com.runmate.exception;

public class MemberNotIncludedTeamException extends RuntimeException {
    public MemberNotIncludedTeamException() {
    }

    public MemberNotIncludedTeamException(String message) {
        super(message);
    }
}
