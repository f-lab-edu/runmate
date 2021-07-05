package com.runmate.service;

import com.runmate.domain.redis.TeamInfo;
import com.runmate.exception.NotFoundTeamInfoException;
import com.runmate.repository.redis.TeamInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class JudgeRunningDataService {
    private final TeamInfoRepository teamInfoRepository;

    public boolean isTeamLeader(long teamId, long memberId) {
        TeamInfo teamInfo = findTeamInfo(teamId);
        return teamInfo.getTeamId() == memberId;
    }

    public boolean isTeamSuccessOnRunning(long teamId) {
        TeamInfo teamInfo = findTeamInfo(teamId);
        return teamInfo.isSuccessOnRunning();
    }

    public boolean isTeamTimeOver(long teamId) {
        TeamInfo teamInfo = findTeamInfo(teamId);
        return teamInfo.isTimeOver();
    }

    public boolean isTeamFailOnRunning(long teamId) {
        TeamInfo teamInfo = findTeamInfo(teamId);
        return teamInfo.isFailOnRunning();
    }

    private TeamInfo findTeamInfo(long teamId) {
        return teamInfoRepository.findById(teamId).orElseThrow(NotFoundTeamInfoException::new);
    }
}
