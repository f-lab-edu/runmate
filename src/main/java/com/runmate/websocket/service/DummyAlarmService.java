package com.runmate.websocket.service;

import com.runmate.websocket.domain.RunningMessage;
import org.springframework.stereotype.Service;

@Service
public class DummyAlarmService {
    public boolean determineSendToAllUsers(RunningMessage message){
        return true;
    }
}
