package com.runmate.service;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.redis.TeamInfo;
import com.runmate.domain.running.Goal;
import com.runmate.domain.running.Team;
import com.runmate.domain.running.TeamMember;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.redis.MemberInfoRepository;
import com.runmate.repository.redis.TeamInfoRepository;
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

        teamMembers.forEach(teamMember -> {
            teamMemberRepository.delete(teamMember);
        });
        teamRepository.delete(team);
        crewUsers.forEach(crewUser -> {
            crewUserRepository.delete(crewUser);
        });
        crewRepository.delete(crew);
        users.forEach(user -> {
            userRepository.delete(user);
        });
    }

    @Test
    void When_PersistRunningDataToMem_Initialize_Redis() {
        //given
        TestTransaction.flagForCommit();
        Goal goal = Goal.builder()
                .totalDistance(10.0F)
                .totalRunningSeconds(3600L)
                .startedAt(LocalDateTime.now().plus(10, ChronoUnit.HOURS))
                .build();
        team = Team.builder()
                .title("test team")
                .goal(goal)
                .build();
        teamRepository.save(team);

        final int numOfUser = 5;
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
        redisTemplate.exec();
        TestTransaction.end();

        //when
        teamMembers.forEach(teamMember -> runningDataMoveService.persistRunningDataToMem(team.getId(), teamMember.getId()));

        //then
        TeamInfo teamInfo = teamInfoRepository.findById(team.getId()).orElse(null);

        assertEquals(teamInfo.getMembers().size(), teamMembers.size());
        for (int i = 0; i < teamInfo.getMembers().size(); i++) {
            assertEquals(teamInfo.getMembers().get(i), teamMembers.get(i).getId());
        }

        assertEquals(teamInfo.getGoal().getDistance(), team.getGoal().getTotalDistance());
        assertEquals(teamInfo.getGoal().getRunningSeconds(), team.getGoal().getTotalRunningSeconds());

        teamMembers.forEach(teamMember -> {
            assertNotNull(memberInfoRepository.findById(teamMember.getId()).orElse(null));
        });
    }
}
