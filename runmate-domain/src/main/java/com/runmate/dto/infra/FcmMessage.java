package com.runmate.dto.infra;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class FcmMessage {
    private final boolean validateOnly;
    private final Message message;

    @Builder
    @Getter
    @RequiredArgsConstructor
    public static class Message {
        private final Notification notification;
        private final String token;
    }

    @Builder
    @Getter
    @RequiredArgsConstructor
    public static class Notification {
        private final String title;
        private final String body;
        private final String image;
    }
}
