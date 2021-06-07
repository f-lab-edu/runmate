package com.runmate.websocket.service;

import com.runmate.websocket.controller.Sender;
import com.runmate.websocket.domain.AlertMessage;
import com.runmate.websocket.domain.RunningMessage;
import com.runmate.websocket.domain.Team;
import com.runmate.websocket.domain.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@RequiredArgsConstructor
@Service
public class AlertService {

    private final TeamRepository teamRepository;

    public void sendAlert(SimpMessagingTemplate simpMessagingTemplate, RunningMessage message, Sender sender) {
        Team team = teamRepository.findById(message.getTeamId()).orElseThrow(IllegalArgumentException::new);
        LocalTime targetPace = team.getTargetPace();
        if (message.getAveragePace().isAfter(targetPace)) {
            AlertMessage alertMessage = AlertMessage.of(message, targetPace);
            sender.send(simpMessagingTemplate, alertMessage);
        }
    }
}
