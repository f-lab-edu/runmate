package com.runmate.service;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.redis.GoalForTempStore;
import com.runmate.domain.redis.MemberInfo;
import com.runmate.domain.redis.TeamInfo;
import com.runmate.domain.running.Goal;
import com.runmate.domain.running.Team;
import com.runmate.domain.running.TeamMember;
import com.runmate.domain.user.User;
import com.runmate.repository.redis.MemberInfoRepository;
import com.runmate.repository.redis.TeamInfoRepository;
import com.runmate.repository.running.TeamMemberRepository;
import com.runmate.repository.running.TeamRepository;
import com.runmate.texture.TextureFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
public class RunningDataMoveToDiskTest {
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeamMemberRepository teamMemberRepository;
    @Autowired
    TextureFactory textureFactory;
    @Autowired
    RunningDataMoveService runningDataMoveService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    TeamInfoRepository teamInfoRepository;
    @Autowired
    MemberInfoRepository memberInfoRepository;
    Team team;
    TeamInfo teamInfo;
    List<Long> memberIds = new ArrayList<>();
    List<MemberInfo> memberInfos = new ArrayList<>();

    @BeforeEach
    void loadData() {
        //init rdbms
        final int numOfMember = 5;
        final long goalSeconds = 3800L;
        final float goalDistance = 10F;
        final LocalDateTime startedAt = now().minus(1, ChronoUnit.HOURS);
        final int adminIndex = 0;
        memberIds = new ArrayList<>();
        memberInfos = new ArrayList<>();

        Goal goal = Goal.builder()
                .totalRunningSeconds(goalSeconds)
                .totalDistance(goalDistance)
                .startedAt(startedAt)
                .build();

        team = Team.builder()
                .title("every day running")
                .goal(goal)
                .build();
        teamRepository.save(team);

        for (int i = 0; i < numOfMember; i++) {
            Crew crew = textureFactory.makeCrew(true);
            User user = textureFactory.makeUser(i + "random@random", true);
            CrewUser crewUser = textureFactory.makeCrewUser(crew, user, true);

            TeamMember teamMember = TeamMember.builder()
                    .team(team)
                    .crewUser(crewUser)
                    .build();

            teamMemberRepository.save(teamMember);
            memberIds.add(teamMember.getId());
        }
        //init redis
        GoalForTempStore goalForTempStore = GoalForTempStore.builder()
                .runningSeconds(goal.getTotalRunningSeconds())
                .startedAt(goal.getStartedAt())
                .distance(goal.getTotalDistance())
                .build();

        teamInfo = TeamInfo.builder()
                .teamId(team.getId())
                .adminId(memberIds.get(adminIndex))
                .goal(goalForTempStore)
                .totalMembers(memberIds)
                .build();
        teamInfo.increaseTotalDistance(0.5F);

        memberIds.forEach(memberId -> {
            MemberInfo memberInfo = MemberInfo.builder()
                    .memberId(memberId)
                    .teamId(teamInfo.getTeamId())
                    .build();
            memberInfo.increaseTotalDistance(0.1F);

            teamInfo.getOnlineMembers().add(memberId);
            memberInfoRepository.save(memberInfo);
        });
        teamInfoRepository.save(teamInfo);
        redisTemplate.exec();
    }

    @AfterEach
    void clearAllData() {
        Iterator<String> iterator = redisTemplate.keys("*").iterator();
        while (iterator.hasNext()) {
            redisTemplate.delete(iterator.next());
        }
    }

    @Test
    void When_PersistRunningDataToDist_Update_RDBMS_Delete_Redis() {
        //when
        runningDataMoveService.persistRunningResultToDisk(team.getId());

        //check rdbms
        team = teamRepository.findById(team.getId()).get();
        assertEquals(team.getResult().getTotalDistance(), teamInfo.getTotalDistance());

        memberInfos.forEach(memberInfo -> {
            TeamMember teamMember = teamMemberRepository.findById(memberInfo.getMemberId()).get();
            assertEquals(teamMember.getResult().getTotalDistance(), memberInfo.getTotalDistance());
        });

        //check redis
        assertNull(teamInfoRepository.findById(team.getId()).orElse(null));

        memberIds.forEach(memberId -> {
            assertNull(memberInfoRepository.findById(memberId).orElse(null));
        });
    }
}
