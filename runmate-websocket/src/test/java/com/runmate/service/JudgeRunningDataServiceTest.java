package com.runmate.service;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.redis.GoalForTempStore;
import com.runmate.redis.TeamInfo;
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
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
public class JudgeRunningDataServiceTest {
    @Autowired
    JudgeRunningDataService judgeRunningDataService;
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
    void When_SuccessOnRunning_Expect_True() {
        //given
        final LocalDateTime startedAt = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        final GoalForTempStore goal = GoalForTempStore.builder()
                .runningSeconds(3700)
                .distance(2F)
                .startedAt(startedAt)
                .build();
        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(1L)
                .goal(goal)
                .adminId(1L)
                .build();
        teamInfo.increaseTotalDistance(2.1F);

        teamInfoRepository.save(teamInfo);
        redisTemplate.exec();

        //then
        assertEquals(true, judgeRunningDataService.isTeamSuccessOnRunning(teamInfo.getTeamId()));
    }

    @Test
    void When_FailOnRunning_Expect_True() {
        //given
        final LocalDateTime startedAt = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        final GoalForTempStore goal = GoalForTempStore.builder()
                .runningSeconds(3500)
                .distance(2F)
                .startedAt(startedAt)
                .build();
        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(1L)
                .goal(goal)
                .adminId(1L)
                .build();
        teamInfo.increaseTotalDistance(1.9F);

        teamInfoRepository.save(teamInfo);
        redisTemplate.exec();

        //then
        assertEquals(true, judgeRunningDataService.isTeamFailOnRunning(teamInfo.getTeamId()));
    }

    @Test
    void When_TimeOver_Expect_True() {
        //given
        final LocalDateTime startedAt = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        final GoalForTempStore goal = GoalForTempStore.builder()
                .runningSeconds(3500)
                .distance(2F)
                .startedAt(startedAt)
                .build();
        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(1L)
                .goal(goal)
                .adminId(1L)
                .build();
        teamInfo.increaseTotalDistance(1.9F);

        teamInfoRepository.save(teamInfo);
        redisTemplate.exec();

        //then
        assertEquals(true, judgeRunningDataService.isTeamTimeOver(teamInfo.getTeamId()));
    }

    @Test
    void When_isTeamLeader() {
        final Long leaderId = 1L;
        final Long normalId = 2L;
        final List<Long> members = Arrays.asList(1L, 2L);
        final LocalDateTime startedAt = LocalDateTime.now().minus(1, ChronoUnit.HOURS);

        final GoalForTempStore goal = GoalForTempStore.builder()
                .runningSeconds(3500)
                .distance(2F)
                .startedAt(startedAt)
                .build();

        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(1L)
                .goal(goal)
                .adminId(leaderId)
                .totalMembers(members)
                .build();

        teamInfoRepository.save(teamInfo);
        redisTemplate.exec();

        assertEquals(true, judgeRunningDataService.isTeamLeader(teamInfo.getTeamId(), leaderId));
        assertEquals(false, judgeRunningDataService.isTeamLeader(teamInfo.getTeamId(), normalId));
    }
}
