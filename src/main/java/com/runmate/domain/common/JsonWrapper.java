package com.runmate.domain.common;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonWrapper {
    private Object data;
    private String error;

    @Builder
    public JsonWrapper(Object data, String error) {
        this.data = data;
        this.error = error;
    }
}
