package com.runmate.controller;

import com.runmate.dto.RunningMessage;
import com.runmate.service.RunningDataManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RunningMessageController {
    private final RunningDataManageService runningDataManageService;
    private final RedisTemplate<String, Object> redisTemplate;

    @MessageMapping("/running")
    public void message(RunningMessage message) {
        RunningMessage updatedMessage = runningDataManageService.updateRunningData(message);
        redisTemplate.convertAndSend("/topic/running", updatedMessage);
    }
}