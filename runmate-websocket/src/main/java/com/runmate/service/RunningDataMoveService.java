package com.runmate.service;

import com.runmate.domain.redis.GoalForTempStore;
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

        teamInfo.getMembers().forEach(memberId -> {
            MemberInfo memberInfo = memberInfoRepository.findById(memberId).orElseThrow(NotFoundMemberInfoException::new);
            TeamMember teamMember = teamMemberRepository.findById(memberId).orElseThrow(NotFoundTeamMemberException::new);
            teamMember.decideResult(teamInfo.getRunningSeconds(), memberInfo.getTotalDistance());

            deleteKeys.add(memberKey + ":" + memberId);
        });
        redisTemplate.delete(deleteKeys);
    }

    public void persistRunningDataToMem(Long teamId, Long memberId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NotFoundTeamException::new);

        TeamInfo teamInfo = teamInfoRepository.findById(teamId).orElse(null);

        if (teamInfo == null) {
            GoalForTempStore goal = GoalForTempStore.builder()
                    .runningSeconds(team.getGoal().getTotalRunningSeconds())
                    .distance(team.getGoal().getTotalDistance())
                    .startedAt(team.getGoal().getStartedAt())
                    .build();

            teamInfo = TeamInfo.builder()
                    .teamId(teamId)
                    .goal(goal)
                    .build();
        }

        teamInfo.getMembers().add(memberId);
        teamInfoRepository.save(teamInfo);

        if (memberInfoRepository.findById(memberId).orElse(null) == null) {
            MemberInfo memberInfo = MemberInfo.builder()
                    .memberId(memberId)
                    .teamId(teamId)
                    .build();
            memberInfoRepository.save(memberInfo);
        }
    }
}