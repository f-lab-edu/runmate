package com.runmate.utils;

import com.runmate.service.JudgeRunningDataService;
import com.runmate.service.RunningDataMoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final RunningDataMoveService runningDataMoveService;
    private final JudgeRunningDataService judgeRunningDataService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final String TEAM_ID_HEADER = "teamId";
    private static final String MEMBER_ID_HEADER = "memberId";

    @EventListener
    public void handleWebSocketDisconnectEvent(SessionDisconnectEvent disconnectEvent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(disconnectEvent.getMessage(), StompHeaderAccessor.class);

        if (!checkConnectStatus(accessor))
            return;

        Long teamId = (Long) accessor.getSessionAttributes().get("teamId");
        Long memberId = (Long) accessor.getSessionAttributes().get("memberId");

        if (judgeRunningDataService.isTeamLeader(teamId, memberId) && judgeRunningDataService.isTeamFinishedRunning(teamId))
            runningDataMoveService.persistRunningResultToDisk(teamId);
    }

    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectEvent connectEvent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(connectEvent.getMessage(), StompHeaderAccessor.class);
        final Long teamId = parseHeaderValueFromStompHeader(accessor, TEAM_ID_HEADER);
        final Long memberId = parseHeaderValueFromStompHeader(accessor, MEMBER_ID_HEADER);

        accessor.getSessionAttributes().put(TEAM_ID_HEADER, teamId);
        accessor.getSessionAttributes().put(MEMBER_ID_HEADER, memberId);
        accessor.getSessionAttributes().put("connect-status", true);

        try {
            runningDataMoveService.persistRunningDataToMem(teamId, memberId);
        } catch (RuntimeException e) {
            accessor.getSessionAttributes().put("connect-status", false);
        }
    }

    @EventListener
    public void handleWebSocketSubscribeEvent(SessionSubscribeEvent subscribeEvent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(subscribeEvent.getMessage(), StompHeaderAccessor.class);

        boolean connect = (boolean) accessor.getSessionAttributes().get("connect-status");
        long teamId = (long) accessor.getSessionAttributes().get(TEAM_ID_HEADER);
        simpMessagingTemplate.convertAndSend("/topic/" + teamId, connect);
    }

    private Long parseHeaderValueFromStompHeader(StompHeaderAccessor accessor, String key) {
        return Long.parseLong(accessor.getFirstNativeHeader(key));
    }

    private boolean checkConnectStatus(StompHeaderAccessor accessor) {
        return (boolean) accessor.getSessionAttributes().get("connect-status");
    }
}
