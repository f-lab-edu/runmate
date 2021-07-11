package com.runmate.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TeamInfoRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    public static final String teamKey = "running:team";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${spring.redis.ttl}")
    private long timeToLive;


    public TeamInfo save(TeamInfo teamInfo) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(teamKey + ":" + teamInfo.getTeamId(), teamInfo, timeToLive, TimeUnit.SECONDS);
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
