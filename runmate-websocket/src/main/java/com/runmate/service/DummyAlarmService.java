package com.runmate.service;

import com.runmate.dto.RunningMessage;
import org.springframework.stereotype.Service;

@Service
public class DummyAlarmService {
    public boolean determineSendToAllUsers(RunningMessage message){
        return true;
    }
}
