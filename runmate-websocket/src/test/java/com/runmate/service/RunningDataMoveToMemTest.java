package com.runmate.service;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.redis.TeamInfo;
import com.runmate.domain.running.Goal;
import com.runmate.domain.running.Team;
import com.runmate.domain.running.TeamMember;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.redis.MemberInfoRepository;
import com.runmate.redis.TeamInfoRepository;
import com.runmate.repository.running.TeamMemberRepository;
import com.runmate.repository.running.TeamRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.texture.TextureFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
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
public class RunningDataMoveToMemTest {
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

    @Autowired
    CrewRepository crewRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CrewUserRepository crewUserRepository;
    Crew crew;
    List<CrewUser> crewUsers = new ArrayList<>();
    List<User> users = new ArrayList<>();
    List<TeamMember> teamMembers = new ArrayList<>();
    Team team;

    @AfterEach
    void clearAllData() {
        Iterator<String> iterator = redisTemplate.keys("*").iterator();
        while (iterator.hasNext()) {
            redisTemplate.delete(iterator.next());
        }
        team.assignLeader(null);
        teamRepository.save(team);

        teamMembers.forEach(teamMember -> {
            teamMemberRepository.delete(teamMember);
        });
        crewUsers.forEach(crewUser -> {
            crewUserRepository.delete(crewUser);
        });
        crewRepository.delete(crew);
        users.forEach(user -> {
            userRepository.delete(user);
        });
        teamRepository.delete(team);
    }

    @Test
    void When_PersistRunningDataToMem_Initialize_Redis() {
        //given
        final LocalDateTime startedAt = now().minus(1, ChronoUnit.HOURS);
        final Long runningTime = 3700L;
        final int numOfUser = 5;
        final int indexOfAdmin = 0;

        TestTransaction.flagForCommit();
        Goal goal = Goal.builder()
                .totalDistance(10.0F)
                .totalRunningSeconds(runningTime)
                .startedAt(startedAt)
                .build();
        team = Team.builder()
                .title("test team")
                .goal(goal)
                .build();
        teamRepository.save(team);

        crew = textureFactory.makeCrew(true);
        for (int i = 0; i < numOfUser; i++) {
            User user = textureFactory.makeUser(i + "ran@ran.com", true);
            CrewUser crewUser = textureFactory.makeCrewUser(crew, user, true);

            crewUsers.add(crewUser);
            users.add(user);

            TeamMember teamMember = TeamMember.builder()
                    .team(team)
                    .crewUser(crewUser)
                    .build();
            teamMemberRepository.save(teamMember);
            teamMembers.add(teamMember);
        }

        team.assignLeader(teamMembers.get(indexOfAdmin));
        teamRepository.save(team);

        TestTransaction.end();

        //when
        teamMembers.forEach(teamMember -> runningDataMoveService.persistRunningDataToMem(team.getId(), teamMember.getId()));

        //then
        TeamInfo teamInfo = teamInfoRepository.findById(team.getId()).orElse(null);

        assertEquals(teamInfo.getOnlineMembers().size(), teamMembers.size());
        for (int i = 0; i < teamInfo.getOnlineMembers().size(); i++) {
            assertEquals(teamInfo.getOnlineMembers().get(i), teamMembers.get(i).getId());
        }

        assertEquals(teamInfo.getGoal().getDistance(), team.getGoal().getTotalDistance());
        assertEquals(teamInfo.getGoal().getRunningSeconds(), team.getGoal().getTotalRunningSeconds());

        teamMembers.forEach(teamMember -> {
            assertNotNull(memberInfoRepository.findById(teamMember.getId()).orElse(null));
        });
    }
}
