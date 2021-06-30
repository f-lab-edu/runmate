package com.runmate.domain.running;

import com.runmate.domain.crew.CrewUser;
import com.runmate.dto.running.TeamCreationRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team")
public class Team {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "leader_id")
    @OneToOne(cascade = CascadeType.ALL)
    private TeamMember leader;

    @Column(name = "title")
    private String title;

    @Embedded
    private Goal goal;

    @Embedded
    private Result result;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<TeamMember> teamMembers = new ArrayList<>();

    @Column(name = "team_status")
    @Enumerated(EnumType.STRING)
    private TeamStatus teamStatus;

    @Builder
    public Team(TeamMember leader, String title, Goal goal) {
        this.leader = leader;
        this.title = title;
        this.goal = goal;
        this.result = Result.builder()
                .totalDistance(0F)
                .totalRunningSeconds(0L)
                .build();

        this.teamStatus = TeamStatus.PENDING;
    }

    public static Team from(TeamCreationRequest request) {
        Goal goal = Goal.builder()
                .totalDistance(request.getGoal().getDistance())
                .totalRunningSeconds(request.getGoal().getRunningTime())
                .startedAt(request.getStartTime())
                .build();

        return Team.builder()
                .leader(null)
                .title(request.getTitle())
                .goal(goal)
                .build();
    }

    public void assignLeader(TeamMember leader) {
        this.leader = leader;
    }

    public void validateMember(CrewUser crewUser) {
        if (leader.isDifferentCrew(crewUser)) {
            throw new IllegalArgumentException("cannot invite to different crew members");
        }
    }

    public void decideResult(float distance, long runningSeconds, boolean isSuccess) {
        this.result = Result.builder()
                .totalDistance(distance)
                .totalRunningSeconds(runningSeconds)
                .build();

        this.teamStatus = isSuccess ? TeamStatus.SUCCESS : TeamStatus.FAIL;
    }
}