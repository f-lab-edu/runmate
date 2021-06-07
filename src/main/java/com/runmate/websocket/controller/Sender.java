package com.runmate.websocket.controller;

import com.runmate.websocket.domain.AlertMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface Sender {
    void send(SimpMessagingTemplate simpMessagingTemplate, AlertMessage message);
}
