package com.runmate.service;

import com.runmate.redis.MemberInfo;
import com.runmate.redis.TeamInfo;
import com.runmate.dto.MessageType;
import com.runmate.dto.RunningMessage;
import com.runmate.exception.NotFoundMemberInfoException;
import com.runmate.exception.NotFoundTeamInfoException;
import com.runmate.redis.MemberInfoRepository;
import com.runmate.redis.TeamInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.runmate.redis.MemberInfoRepository.*;
import static com.runmate.redis.TeamInfoRepository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RunningDataManageService {
    private final TeamInfoRepository teamInfoRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public RunningMessage updateRunningData(RunningMessage message) {
        updateMemberInfo(message);
        TeamInfo teamInfo = updateTeamInfo(message);
        return assignMessageType(message, teamInfo);
    }

    private MemberInfo updateMemberInfo(RunningMessage message) {
        long memberId = message.getMemberId();
        MemberInfo memberInfo = memberInfoRepository.findById(memberId).orElseThrow(NotFoundMemberInfoException::new);
        memberInfo.increaseTotalDistance(message.getDistance());
        memberInfoRepository.save(memberInfo);
        return memberInfo;
    }

    private TeamInfo updateTeamInfo(RunningMessage message) {
        long teamId = message.getTeamId();
        TeamInfo teamInfo = teamInfoRepository.findById(teamId).orElseThrow(NotFoundTeamInfoException::new);
        teamInfo.increaseTotalDistance(message.getDistance());
        teamInfoRepository.save(teamInfo);
        return teamInfo;
    }

    private RunningMessage assignMessageType(RunningMessage message, TeamInfo teamInfo) {
        if (teamInfo.isFailOnRunning())
            message.changeMessageType(MessageType.FAIL);
        else if (teamInfo.isSuccessOnRunning()) {
            message.changeMessageType(MessageType.SUCCESS);
        } else {
            message.changeMessageType(MessageType.RUNNING);
        }
        return message;
    }

    public void clearAllRunningData(long teamId, long memberId) {
        List<String> deleteKeys = new ArrayList<>();
        teamInfoRepository.findById(teamId).ifPresent(teamInfo -> deleteKeys.add(teamKey + ":" + teamId));
        deleteKeys.add(memberKey + ":" + memberId);
        redisTemplate.delete(deleteKeys);
    }
}
