package com.runmate.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.domain.redis.MemberInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class MemberInfoRepositoryTest {
    @Autowired
    MemberInfoRepository memberInfoRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    RedisTemplate<String, Object> redisTemplate;


    @Test
    void When_SaveAndFind_MemberInfo_Expect_SameObject() {
        //when
        MemberInfo memberInfo = MemberInfo.builder()
                .teamId(3L)
                .memberId(2L)
                .build();
        memberInfo.increaseTotalDistance(10.0F);

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set("running:member:" + memberInfo.getMemberId(), memberInfo);
        redisTemplate.exec();


        MemberInfo result = objectMapper.convertValue(ops.get("running:member:" + memberInfo.getMemberId()), MemberInfo.class);

        //then
        checkSameMemberInfo(memberInfo, result);
        redisTemplate.delete("running:member:" + memberInfo.getMemberId());
    }

    void checkSameMemberInfo(MemberInfo one, MemberInfo another) {
        assertEquals(one.getMemberId(), another.getMemberId());
        assertEquals(one.getTeamId(), another.getTeamId());
        assertEquals(one.getTotalDistance(), another.getTotalDistance());
    }
}
