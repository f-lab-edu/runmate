package com.runmate.websocket.controller;

import com.runmate.websocket.domain.RunningMessage;
import com.runmate.websocket.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RunningMessageController {
    private final AlertService alarmService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/running")
    public void sendOwnInfo(RunningMessage message) {
        Long teamId = message.getTeamId();
        alarmService.sendAlert(simpMessagingTemplate, message,
                (messagingTemplate, alertMessage) -> messagingTemplate.convertAndSend("/topic/alert/" + teamId, alertMessage));
        simpMessagingTemplate.convertAndSend("/topic/info/" + teamId, message);
    }
}