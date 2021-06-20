package com.runmate.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.domain.redis.GoalForTempStore;
import com.runmate.domain.redis.TeamInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class TeamInfoRepositoryTest {
    GoalForTempStore goal;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        goal = GoalForTempStore.builder()
                .distance(42.195F)
                .runningSeconds(1200000)
                .startedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void when_SaveAndFind_WithJacksonSerializer() {
        List<Long> memberIds = Arrays.asList(2L, 3L, 6L);
        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(2L)
                .adminId(3L)
                .goal(goal)
                .build();

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set("running:team:" + teamInfo.getTeamId(), teamInfo);
        redisTemplate.exec();

        TeamInfo result = objectMapper.convertValue(ops.get("running:team:" + teamInfo.getTeamId()), TeamInfo.class);
        checkSameTeamInfo(teamInfo, result);

        redisTemplate.delete("running:team:"+teamInfo.getTeamId());
    }

    void checkSameTeamInfo(TeamInfo one, TeamInfo another) {
        assertEquals(one.getTeamId(), another.getTeamId());
        assertEquals(one.getTotalDistance(), another.getTotalDistance());
        assertEquals(one.getAdminId(), another.getAdminId());
        assertEquals(one.getGoal(), another.getGoal());
    }
}
