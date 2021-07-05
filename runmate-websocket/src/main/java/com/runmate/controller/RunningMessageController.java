package com.runmate.controller;

import com.runmate.dto.RunningMessage;
import com.runmate.service.RunningDataManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RunningMessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RunningDataManageService runningDataManageService;

    @MessageMapping("/running")
    public void message(RunningMessage message, SimpMessageHeaderAccessor headerAccessor) {
        RunningMessage updatedMessage = runningDataManageService.updateRunningData(message);
        simpMessagingTemplate.convertAndSend("/topic/" + message.getTeamId(), updatedMessage);
    }
}