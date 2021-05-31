package com.runmate.utils;

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

    public static JsonWrapper success(Object data) {
        return JsonWrapper.builder()
                .data(data)
                .error(null)
                .build();
    }

    public static JsonWrapper error(String error) {
        return JsonWrapper.builder()
                .data(null)
                .error(error)
                .build();
    }
}
