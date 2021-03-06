package com.runmate.service;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.redis.GoalForTempStore;
import com.runmate.redis.MemberInfo;
import com.runmate.redis.TeamInfo;
import com.runmate.domain.running.Position;
import com.runmate.dto.RunningMessage;
import com.runmate.redis.MemberInfoRepository;
import com.runmate.redis.TeamInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
public class RunningDataManageServiceTest {
    @Autowired
    RunningDataManageService runningDataManageService;
    @Autowired
    MemberInfoRepository memberInfoRepository;
    @Autowired
    TeamInfoRepository teamInfoRepository;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void clearAllRedisData() {
        Iterator<String> iterator = redisTemplate.keys("*").iterator();
        while (iterator.hasNext()) {
            redisTemplate.delete(iterator.next());
        }
    }

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
                .goal(goal)
                .build();
        teamInfo.increaseTotalDistance(3.0F);
        teamInfoRepository.save(teamInfo);
        redisTemplate.exec();

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

    @Test
    void When_clearRunningData_Expect_AllRunningDataDeleted() {
        //given
        final int numOfMembers = 5;
        final long teamId = 7L;
        final long adminId = 0L;
        List<Long> memberIds = Arrays.asList(0L, 1L, 2L, 3L, 4L);

        memberIds.forEach(memberId -> {
            MemberInfo memberInfo = MemberInfo.builder()
                    .memberId(memberId)
                    .teamId(teamId)
                    .build();
            memberInfoRepository.save(memberInfo);
        });
        GoalForTempStore goal = GoalForTempStore.builder()
                .runningSeconds(3600)
                .startedAt(LocalDateTime.now())
                .distance(10.0F)
                .build();

        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(teamId)
                .adminId(adminId)
                .goal(goal)
                .build();
        teamInfoRepository.save(teamInfo);
        redisTemplate.exec();

        //when
        memberIds.forEach(memberId -> {
            runningDataManageService.clearAllRunningData(teamId, memberId);
        });

        //then
        memberIds.forEach(memberId -> {
            assertFalse(memberInfoRepository.findById(memberId).isPresent());
        });

        assertFalse(teamInfoRepository.findById(teamId).isPresent());
    }
}
