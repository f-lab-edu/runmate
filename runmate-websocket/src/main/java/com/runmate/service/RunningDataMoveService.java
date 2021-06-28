package com.runmate.service;

import com.runmate.domain.redis.MemberInfo;
import com.runmate.domain.redis.TeamInfo;
import com.runmate.domain.running.Team;
import com.runmate.domain.running.TeamMember;
import com.runmate.exception.*;
import com.runmate.repository.redis.MemberInfoRepository;
import com.runmate.repository.redis.TeamInfoRepository;
import com.runmate.repository.running.TeamMemberRepository;
import com.runmate.repository.running.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.runmate.repository.redis.MemberInfoRepository.memberKey;
import static com.runmate.repository.redis.TeamInfoRepository.teamKey;

@Service
@RequiredArgsConstructor
@Transactional
public class RunningDataMoveService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInfoRepository teamInfoRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void persistRunningResultToDisk(Long teamId) {
        List<String> deleteKeys = new ArrayList<>();

        TeamInfo teamInfo = teamInfoRepository.findById(teamId).orElseThrow(NotFoundTeamInfoException::new);
        deleteKeys.add(teamKey + ":" + teamId);

        Team team = teamRepository.findById(teamId).orElseThrow(NotFoundTeamException::new);
        team.decideResult(teamInfo.getTotalDistance(), teamInfo.getRunningSeconds(), teamInfo.isSuccessOnRunning());

        teamInfo.getTotalMembers().forEach(memberId -> {
            MemberInfo memberInfo = memberInfoRepository.findById(memberId).orElseThrow(NotFoundMemberInfoException::new);
            TeamMember teamMember = teamMemberRepository.findById(memberId).orElseThrow(NotFoundTeamMemberException::new);
            teamMember.decideResult(teamInfo.getRunningSeconds(), memberInfo.getTotalDistance());

            deleteKeys.add(memberKey + ":" + memberId);
        });
        redisTemplate.delete(deleteKeys);
    }

    public void persistRunningDataToMem(Long teamId, Long memberId) {
        TeamInfo teamInfo = teamInfoRepository.findById(teamId).orElse(null);
        if (teamInfo == null) {
            Team team = teamRepository.findById(teamId).orElseThrow(NotFoundTeamException::new);
            teamInfo = TeamInfo.fromGoal()
                    .teamId(teamId)
                    .goal(team.getGoal())
                    .build();
        }

        teamInfo.participateRunning(memberId);
        teamInfoRepository.save(teamInfo);

        if (memberInfoRepository.findById(memberId).equals(Optional.empty())) {
            persistMemberInfoDataToMem(teamId, memberId);
        }
    }

    private void persistMemberInfoDataToMem(Long teamId, Long memberId) {
        MemberInfo memberInfo = MemberInfo.builder()
                .memberId(memberId)
                .teamId(teamId)
                .build();
        memberInfoRepository.save(memberInfo);
    }
}