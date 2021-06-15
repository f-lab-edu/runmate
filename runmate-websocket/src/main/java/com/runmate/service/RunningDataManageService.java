package com.runmate.service;

import com.runmate.domain.redis.MemberInfo;
import com.runmate.domain.redis.TeamInfo;
import com.runmate.dto.RunningMessage;
import com.runmate.exception.NotFoundMemberInfoException;
import com.runmate.exception.NotFoundTeamInfoException;
import com.runmate.repository.redis.MemberInfoRepository;
import com.runmate.repository.redis.TeamInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RunningDataManageService {
    private final TeamInfoRepository teamInfoRepository;
    private final MemberInfoRepository memberInfoRepository;

    public void updateRunningData(RunningMessage message) {
        updateMemberInfo(message);
        updateTeamInfo(message);
    }

    private void updateMemberInfo(RunningMessage message) {
        long memberId = message.getMemberId();
        MemberInfo memberInfo = memberInfoRepository.findById(memberId).orElseThrow(NotFoundMemberInfoException::new);
        memberInfo.increaseTotalDistance(message.getDistance());
        memberInfoRepository.save(memberInfo);
    }

    private void updateTeamInfo(RunningMessage message) {
        long teamId = message.getTeamId();
        TeamInfo teamInfo = teamInfoRepository.findById(teamId).orElseThrow(NotFoundTeamInfoException::new);
        teamInfo.increaseTotalDistance(message.getDistance());
        teamInfoRepository.save(teamInfo);
    }
}
