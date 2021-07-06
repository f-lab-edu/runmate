package com.runmate.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeamInfoRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    public static final String teamKey = "running:team";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TeamInfo save(TeamInfo teamInfo) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(teamKey + ":" + teamInfo.getTeamId(), teamInfo);

        return teamInfo;
    }

    public Optional<TeamInfo> findById(long teamId) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object result = ops.get(teamKey + ":" + teamId);

        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(objectMapper.convertValue(result, TeamInfo.class));
    }
}
