package com.runmate.repository;

import com.runmate.domain.running.Goal;
import com.runmate.domain.running.Result;
import com.runmate.domain.running.Team;
import com.runmate.repository.running.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class TeamRepositoryTest {
    @Autowired
    TeamRepository teamRepository;

    Goal goal;
    Result result;

    @BeforeEach
    void setUp() {
        goal = Goal.builder()
                .totalRunningSeconds(160000)
                .totalDistance(42.195F)
                .build();
    }

    @Test
    void When_SaveAndGet() {
        final int numOfTeamBeforeSave = teamRepository.findAll().size();

        Team team = Team.builder()
                .goal(goal)
                .title("ok go")
                .build();
        teamRepository.save(team);

        final int numOfTeamAfterSave = teamRepository.findAll().size();

        assertEquals(numOfTeamBeforeSave + 1, numOfTeamAfterSave);

        Team result = teamRepository.findById(team.getId()).get();
        checkSameTeam(team, result);
    }

    @Test
    void When_findByIdHaveTeamMembers() {
        final Long teamId = 1L;
        final int numOfTeamMember = 5;
        Team team = teamRepository.findByIdHaveTeamMembers(teamId).orElse(null);

        assertNotNull(team);
        assertEquals(numOfTeamMember, team.getTeamMembers().size());
    }

    void checkSameTeam(Team one, Team another) {
        assertEquals(one.getId(), another.getId());
        assertEquals(one.getTitle(), another.getTitle());
        assertEquals(one.getGoal(), another.getGoal());
        assertEquals(one.getResult(), another.getResult());
    }
}
