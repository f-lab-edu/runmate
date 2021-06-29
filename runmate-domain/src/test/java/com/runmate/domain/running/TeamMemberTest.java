package com.runmate.domain.running;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TeamMemberTest {

    @Test
    @DisplayName("decideResult 메소드 호출시 개인 기록이 0km, 0초에서 지정된 기록으로 변경")
    void decideResult() {
        //given
        CrewUser crewUser = CrewUser.builder().build();
        Team team = Team.builder().leader(null).title("team").goal(Goal.builder().build()).build();
        TeamMember teamMember = TeamMember.builder().team(team).crewUser(crewUser).build();

        assertThat(teamMember.getResult()).isNotNull();
        assertThat(teamMember.getResult().getTotalDistance()).isEqualTo(0f);
        assertThat(teamMember.getResult().getTotalRunningSeconds()).isEqualTo(0L);

        //when
        teamMember.decideResult(5000L, 5.0f);

        //then
        assertThat(teamMember.getResult()).isNotNull();
        assertThat(teamMember.getResult().getTotalDistance()).isEqualTo(5.0f);
        assertThat(teamMember.getResult().getTotalRunningSeconds()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("같은 Crew 객체로 CrewUser 객체 생성시 isDifferentCrew는 false 반환")
    void Given_SameCrewObject_When_IsDifferentCrew_Then_ReturnFalse() {
        //given
        Crew sameCrew = Crew.builder().build();
        User user = User.of().username("one").build();
        User anotherUser = User.of().username("another").build();

        CrewUser crewUser = CrewUser.builder().crew(sameCrew).user(user).build();
        CrewUser anotherCrewUser = CrewUser.builder().crew(sameCrew).user(anotherUser).build();

        TeamMember leader = TeamMember.builder().crewUser(crewUser).build();

        //when
        boolean isDifferentCrew = leader.isDifferentCrew(anotherCrewUser);

        //then
        assertThat(isDifferentCrew).isFalse();
    }

    @Test
    @DisplayName("다른 Crew 객체로 CrewUser 객체 생성시 isDifferentCrew는 true 반환")
    void Given_DifferentCrewObject_When_IsDifferentCrew_Then_ReturnTrue() {
        //given
        Crew crew = Crew.builder().name("a crew").build();
        Crew anotherCrew = Crew.builder().name("another crew").build();

        User user = User.of().username("one").build();
        User anotherUser = User.of().username("another").build();

        CrewUser crewUser = CrewUser.builder().crew(crew).user(user).build();
        CrewUser anotherCrewUser = CrewUser.builder().crew(anotherCrew).user(anotherUser).build();

        TeamMember leader = TeamMember.builder().crewUser(crewUser).build();

        //when
        boolean isDifferentCrew = leader.isDifferentCrew(anotherCrewUser);

        //then
        assertThat(isDifferentCrew).isTrue();
    }
}
