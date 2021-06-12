package com.runmate.repository.redis;

import com.runmate.domain.redis.TeamInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class TeamInfoRepositoryTest {
    @Autowired
    TeamInfoRepository teamInfoRepository;

    @Test
    void When_SaveAndFind_TeamInfo_Expect_SameObject() {
        //when
        List<Long> memberIds = Arrays.asList(2L, 3L, 6L);
        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(2L)
                .adminId(3L)
                .members(memberIds)
                .build();
        teamInfoRepository.save(teamInfo);

        //then
        TeamInfo result = teamInfoRepository.findById(teamInfo.getTeamId()).get();
        checkSameTeamInfo(teamInfo, result);
    }

    void checkSameTeamInfo(TeamInfo one, TeamInfo another) {
        assertEquals(one.getTeamId(), another.getTeamId());
        assertEquals(one.getTotalDistance(), another.getTotalDistance());
        assertEquals(one.getAdminId(), another.getAdminId());
    }
}
