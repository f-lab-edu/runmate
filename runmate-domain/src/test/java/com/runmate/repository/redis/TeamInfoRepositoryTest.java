package com.runmate.repository.redis;

import com.runmate.domain.redis.GoalForTempStore;
import com.runmate.domain.redis.TeamInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class TeamInfoRepositoryTest {
    @Autowired
    TeamInfoRepository teamInfoRepository;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Test
    void when_SaveAndFind_WithJacksonSerializer() {
        GoalForTempStore goal = GoalForTempStore.builder()
                .distance(42.195F)
                .runningSeconds(1200000)
                .startedAt(LocalDateTime.now())
                .build();

        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(2L)
                .adminId(3L)
                .goal(goal)
                .build();

        teamInfoRepository.save(teamInfo);
        redisTemplate.exec();

        TeamInfo result = teamInfoRepository.findById(teamInfo.getTeamId()).orElse(null);
        checkSameTeamInfo(teamInfo, result);

        redisTemplate.delete(TeamInfoRepository.teamKey + ":" + result.getTeamId());
    }

    void checkSameTeamInfo(TeamInfo one, TeamInfo another) {
        assertEquals(one.getTeamId(), another.getTeamId());
        assertEquals(one.getTotalDistance(), another.getTotalDistance());
        assertEquals(one.getAdminId(), another.getAdminId());
        assertEquals(one.getGoal(), another.getGoal());
    }
}
