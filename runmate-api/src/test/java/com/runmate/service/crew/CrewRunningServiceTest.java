package com.runmate.service.crew;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.domain.running.*;
import com.runmate.domain.user.User;
import com.runmate.dto.running.TeamCreationRequest;
import com.runmate.dto.running.TeamGoalRequest;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.running.TeamMemberRepository;
import com.runmate.repository.running.TeamRepository;
import com.runmate.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
class CrewRunningServiceTest {

    private @MockBean UserRepository userRepository;
    private @MockBean CrewUserRepository crewUserRepository;
    private @MockBean TeamRepository teamRepository;
    private @MockBean TeamMemberRepository teamMemberRepository;

    private @Autowired CrewRunningService crewRunningService;


    final long LEADER_CREW_USER_ID  = 1L;

    private TeamCreationRequest request;

    @BeforeEach
    void setUp() {
        request = new TeamCreationRequest(
                LEADER_CREW_USER_ID,
                "team",
                new ArrayList<>(),
                new TeamGoalRequest(5.0f, 1500L),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("TeamCreationRequest 객체로 팀 생성시 요청 객체와 일치하는 데이터가 설정되고 리더 CrewUser가 설정")
    void createTeam() {
        //given
        User leaderUser = User.of().build();
        Crew crew = Crew.builder().name("crew").build();
        CrewUser leaderCrewUser = CrewUser.builder().user(leaderUser).crew(crew).role(Role.ADMIN).build();

        Team team = Team.from(request);
        TeamMember leader = TeamMember.builder().team(team).crewUser(leaderCrewUser).build();
        Team mockedTeam = Team.builder()
                .title(request.getTitle())
                .leader(leader)
                .goal(new Goal(request.getGoal().getDistance(), request.getGoal().getRunningTime(), request.getStartTime()))
                .build();

        when(crewUserRepository.findById(LEADER_CREW_USER_ID)).thenReturn(Optional.of(leaderCrewUser));
        when(teamRepository.save(any())).thenReturn(mockedTeam);

        //when
        Team createdTeam = crewRunningService.createTeam(request);

        //then
        assertThat(createdTeam).isEqualTo(mockedTeam);
        assertThat(mockedTeam.getLeader().getCrewUser()).isEqualTo(leaderCrewUser);
        assertThat(mockedTeam.getTeamStatus()).isEqualTo(TeamStatus.PENDING);
        assertThat(mockedTeam.getLeader().getTeamMemberStatus()).isEqualTo(TeamMemberStatus.ACCEPTED);
    }

    @Test
    void addMember() {
    }
}