package com.runmate.service;

import com.runmate.domain.redis.GoalForTempStore;
import com.runmate.domain.redis.MemberInfo;
import com.runmate.domain.redis.TeamInfo;
import com.runmate.domain.running.Position;
import com.runmate.dto.RunningMessage;
import com.runmate.repository.redis.MemberInfoRepository;
import com.runmate.repository.redis.TeamInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RunningDataManageServiceTest {
    @Autowired
    RunningDataManageService runningDataManageService;
    @Autowired
    MemberInfoRepository memberInfoRepository;
    @Autowired
    TeamInfoRepository teamInfoRepository;

    @Test
    public void When_IncreaseDistance() {
        final long memberId = 1L;
        final long teamId = 1L;
        //given
        MemberInfo memberInfo = MemberInfo.builder()
                .memberId(memberId)
                .teamId(teamId)
                .build();
        memberInfo.increaseTotalDistance(3.0F);
        memberInfoRepository.save(memberInfo);

        GoalForTempStore goal = GoalForTempStore.builder()
                .startedAt(LocalDateTime.now())
                .runningSeconds(10000)
                .distance(10.0F)
                .build();

        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(teamId)
                .adminId(memberId)
                .members(Arrays.asList(memberId))
                .goal(goal)
                .build();
        teamInfo.increaseTotalDistance(3.0F);
        teamInfoRepository.save(teamInfo);

        RunningMessage runningMessage = RunningMessage.builder()
                .distance(10.2F)
                .averagePace(LocalTime.of(0, 06, 31))
                .instantaneousPace(LocalTime.of(0, 07, 10))
                .position(new Position(0, 0))
                .teamId(teamId)
                .memberId(memberId)
                .username("name")
                .build();

        final float memberDistanceBeforeIncrease = memberInfo.getTotalDistance();
        final float teamDistanceBeforeIncrease = teamInfo.getTotalDistance();
        //when
        runningDataManageService.updateRunningData(runningMessage);

        memberInfo = memberInfoRepository.findById(memberId).get();
        teamInfo = teamInfoRepository.findById(teamId).get();

        //then
        assertEquals(memberDistanceBeforeIncrease + runningMessage.getDistance(), memberInfo.getTotalDistance());
        assertEquals(teamDistanceBeforeIncrease + runningMessage.getDistance(), teamInfo.getTotalDistance());
    }
}
