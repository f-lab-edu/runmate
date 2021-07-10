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
public class MemberInfoRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${spring.redis.ttl}")
    private long timeToLive;
    public static final String memberKey = "running:member";

    public MemberInfo save(MemberInfo memberInfo) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(memberKey + ":" + memberInfo.getMemberId(), memberInfo, timeToLive, TimeUnit.SECONDS);
        return memberInfo;
    }

    public Optional<MemberInfo> findById(long memberId) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object result = ops.get(memberKey + ":" + memberId);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(objectMapper.convertValue(result, MemberInfo.class));
    }
}
