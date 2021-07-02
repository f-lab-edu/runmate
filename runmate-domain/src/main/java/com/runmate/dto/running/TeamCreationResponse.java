package com.runmate.dto.running;

import com.runmate.domain.running.Team;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamCreationResponse {
    private final long id;
    private final String title;
    private final List<TeamMemberCreationResponse> members;
    private final TeamGoalResponse goal;
    private final LocalDateTime startTime;

    public static TeamCreationResponse of(Team team, List<TeamMemberCreationResponse> members) {
        return new TeamCreationResponse(
                team.getId(),
                team.getTitle(),
                members,
                TeamGoalResponse.from(team.getGoal()),
                team.getGoal().getStartedAt()
        );
    }
}
