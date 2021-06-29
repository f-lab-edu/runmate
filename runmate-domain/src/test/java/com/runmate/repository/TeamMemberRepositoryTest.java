package com.runmate.repository;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.running.*;
import com.runmate.domain.user.User;
import com.runmate.repository.running.TeamMemberRepository;
import com.runmate.repository.running.TeamRepository;
import com.runmate.texture.TextureFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class TeamMemberRepositoryTest {
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeamMemberRepository teamMemberRepository;
    @Autowired
    TextureFactory textureFactory;

    final String email = "random@random.com";
    CrewUser crewUser;
    Crew crew;
    User user;
    Team team;

    @BeforeEach
    void setUp() {
        Goal goal = Goal.builder()
                .totalDistance(3.6F)
                .totalRunningSeconds(30000)
                .build();

        team = Team.builder()
                .goal(goal)
                .title("let' go")
                .build();
        teamRepository.save(team);

        crew = textureFactory.makeCrew(true);
        user = textureFactory.makeUser(email, true);
        crewUser = textureFactory.makeCrewUser(crew, user, true);
    }

    @Test
    public void When_SaveAndGet() {
        final int numOfTeamMemberBeforeSave = teamMemberRepository.findAll().size();
        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .crewUser(crewUser)
                .build();
        teamMemberRepository.save(teamMember);

        final int numOfTeamMemberAfterSave = teamMemberRepository.findAll().size();
        assertEquals(numOfTeamMemberBeforeSave + 1, numOfTeamMemberAfterSave);

        TeamMember result = teamMemberRepository.findById(teamMember.getId()).get();
        checkSameTeamMember(teamMember, result);
    }

    @Test
    public void When_findByIdAndTeam() {
        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .crewUser(crewUser)
                .build();
        teamMemberRepository.save(teamMember);

        //when
        TeamMember result = teamMemberRepository.findByIdAndTeam(teamMember.getId(), team).orElse(null);
        //then
        assertNotNull(result);
        assertEquals(result.getTeam().getId(), team.getId());
    }

    void checkSameTeamMember(TeamMember one, TeamMember another) {
        assertEquals(one.getId(), another.getId());
        assertEquals(one.getTeam().getId(), another.getTeam().getId());
        assertEquals(one.getCrewUser().getId(), another.getCrewUser().getId());
        assertEquals(one.getResult(), another.getResult());
    }
}
