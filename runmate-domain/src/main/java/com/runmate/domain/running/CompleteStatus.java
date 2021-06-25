package com.runmate.domain.running;

import java.util.Arrays;

public enum CompleteStatus {
    SUCCESS(true), FAIL(false);

    private boolean isSuccess;

    CompleteStatus(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean getValue() {
        return isSuccess;
    }

    public static CompleteStatus of(boolean flag) {
        return Arrays.stream(CompleteStatus.values())
                .filter(status -> status.getValue() == flag)
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}

