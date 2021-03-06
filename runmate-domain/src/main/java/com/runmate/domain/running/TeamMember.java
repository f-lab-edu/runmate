package com.runmate.domain.running;

import com.runmate.domain.crew.CrewUser;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team_member")
public class TeamMember {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "crew_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CrewUser crewUser;

    @JoinColumn(name = "team_id")
    @ManyToOne
    private Team team;

    @Embedded
    private IndividualResult result;

    @Column(name = "team_member_status")
    @Enumerated(EnumType.STRING)
    private TeamMemberStatus teamMemberStatus;

    @Builder
    public TeamMember(CrewUser crewUser, Team team) {
        this.crewUser = crewUser;
        this.team = team;
        this.result = IndividualResult.builder()
                .totalDistance(0F)
                .totalRunningSeconds(0L)
                .build();
        this.teamMemberStatus = TeamMemberStatus.PENDING;
    }

    public void decideResult(Long runningSeconds, float distance) {
        this.result = IndividualResult.builder()
                .totalDistance(distance)
                .totalRunningSeconds(runningSeconds)
                .build();
    }

    public void acceptTeamInvitation() {
        this.teamMemberStatus = TeamMemberStatus.ACCEPTED;
    }

    public void rejectTeamInvitation() {
        this.teamMemberStatus = TeamMemberStatus.REJECTED;
    }

    public boolean isDifferentCrew(CrewUser crewUser) {
        return !this.crewUser.getCrew().equals(crewUser.getCrew());
    }
}
