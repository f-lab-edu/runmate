package com.runmate.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.dto.RunningMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            RunningMessage runningMessage = objectMapper.readValue(message.toString(), RunningMessage.class);
            simpMessagingTemplate.convertAndSend("/topic/" + runningMessage.getTeamId(), runningMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
