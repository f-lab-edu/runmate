package com.runmate.utils;

import com.runmate.service.JudgeRunningDataService;
import com.runmate.service.RunningDataMoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final RunningDataMoveService runningDataMoveService;
    private final JudgeRunningDataService judgeRunningDataService;
    private static final String TEAM_ID_HEADER = "teamId";
    private static final String MEMBER_ID_HEADER = "memberId";

    @EventListener
    public void handleWebSocketDisconnectEvent(SessionDisconnectEvent disconnectEvent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(disconnectEvent.getMessage(), StompHeaderAccessor.class);

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

        accessor.getSessionAttributes().put("teamId", teamId);
        accessor.getSessionAttributes().put("memberId", memberId);

        runningDataMoveService.persistRunningDataToMem(teamId, memberId);
    }

    private Long parseHeaderValueFromStompHeader(StompHeaderAccessor accessor, String key) {
        return Long.parseLong(accessor.getFirstNativeHeader(key));
    }
}
