package com.runmate.service;

import com.runmate.redis.MemberInfo;
import com.runmate.redis.TeamInfo;
import com.runmate.domain.running.Team;
import com.runmate.domain.running.TeamMember;
import com.runmate.exception.*;
import com.runmate.redis.MemberInfoRepository;
import com.runmate.redis.TeamInfoRepository;
import com.runmate.repository.running.TeamMemberRepository;
import com.runmate.repository.running.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.runmate.redis.MemberInfoRepository.memberKey;
import static com.runmate.redis.TeamInfoRepository.teamKey;

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
            memberInfoRepository.findById(memberId).ifPresent(memberInfo -> {
                TeamMember teamMember = teamMemberRepository.findById(memberId).orElseThrow(NotFoundTeamMemberException::new);
                teamMember.decideResult(teamInfo.getRunningSeconds(), memberInfo.getTotalDistance());

                deleteKeys.add(memberKey + ":" + memberId);
            });
        });
        redisTemplate.delete(deleteKeys);
    }

    public void persistRunningDataToMem(Long teamId, Long memberId) {
        TeamInfo teamInfo = teamInfoRepository.findById(teamId).orElse(null);
        if (teamInfo == null) {
            Team team = teamRepository.findByIdHaveTeamMembers(teamId).orElseThrow(NotFoundTeamException::new);
            List<Long> totalMembers = team.getTeamMembers()
                    .stream()
                    .map(teamMember -> teamMember.getId())
                    .collect(Collectors.toList());

            teamInfo = TeamInfo.fromGoal()
                    .teamId(teamId)
                    .goal(team.getGoal())
                    .adminId(team.getLeader().getId())
                    .totalMembers(totalMembers)
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