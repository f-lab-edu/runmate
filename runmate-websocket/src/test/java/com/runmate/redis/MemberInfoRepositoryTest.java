package com.runmate.redis;

import com.runmate.TestActiveProfilesResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
public class MemberInfoRepositoryTest {
    @Autowired
    MemberInfoRepository memberInfoRepository;

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

        memberInfoRepository.save(memberInfo);
        redisTemplate.exec();

        MemberInfo result = memberInfoRepository.findById(memberInfo.getMemberId()).orElse(null);

        //then
        checkSameMemberInfo(memberInfo, result);
        redisTemplate.delete(MemberInfoRepository.memberKey + ":" + memberInfo.getMemberId());
    }

    void checkSameMemberInfo(MemberInfo one, MemberInfo another) {
        assertEquals(one.getMemberId(), another.getMemberId());
        assertEquals(one.getTeamId(), another.getTeamId());
        assertEquals(one.getTotalDistance(), another.getTotalDistance());
    }
}
