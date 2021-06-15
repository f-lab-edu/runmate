package com.runmate.controller;

import com.runmate.service.DummyAlarmService;
import com.runmate.dto.RunningMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RunningMessageController {
    private final DummyAlarmService alarmService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/running")
    public void message(RunningMessage message){
        if(alarmService.determineSendToAllUsers(message)){
            simpMessagingTemplate.convertAndSend("/topic/"+message.getTeamId(),message);
        }
    }
}