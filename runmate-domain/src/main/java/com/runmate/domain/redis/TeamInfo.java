package com.runmate.domain.redis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.runmate.domain.running.Goal;
import com.runmate.exception.CurrentIsNotRunningTimeException;
import com.runmate.exception.MemberNotIncludedTeamException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamInfo {
    @Id
    private long teamId;
    private List<Long> onlineMembers = new ArrayList<>();
    private List<Long> totalMembers = new ArrayList<>();
    private float totalDistance;
    private long adminId;
    private GoalForTempStore goal;
    private long runningSeconds;

    @Builder(builderMethodName = "builder")
    public TeamInfo(long teamId, long adminId, GoalForTempStore goal, List<Long> totalMembers) {
        this.teamId = teamId;
        this.goal = goal;

        this.adminId = adminId;
        this.totalMembers = totalMembers;

        this.totalDistance = 0;
        this.runningSeconds = 0;
    }

    @Builder(builderMethodName = "fromGoal", builderClassName = "forMove")
    public TeamInfo(long teamId, long adminId, Goal goal, List<Long> totalMembers) {
        this.teamId = teamId;
        this.adminId = adminId;
        this.totalDistance = 0;
        this.runningSeconds = 0;
        this.totalMembers = totalMembers;

        this.goal = GoalForTempStore.builder()
                .startedAt(goal.getStartedAt())
                .distance(goal.getTotalDistance())
                .runningSeconds(goal.getTotalRunningSeconds())
                .build();
    }

    public float increaseTotalDistance(float distance) {
        this.totalDistance += distance;
        updateRunningSeconds();
        return this.totalDistance;
    }

    @JsonIgnore
    public LocalDateTime getEndTime() {
        return getGoal().getStartedAt().plus(getGoal().getRunningSeconds(), ChronoUnit.SECONDS);
    }

    private void updateRunningSeconds() {
        this.runningSeconds = Duration.between(this.goal.getStartedAt(), now()).getSeconds();
    }

    @JsonIgnore
    public boolean isSuccessOnRunning() {
        return !isTimeOver() && totalDistance >= goal.getDistance();
    }

    @JsonIgnore
    public boolean isTimeOver() {
        return now().isAfter(getEndTime());
    }

    @JsonIgnore
    public boolean isFailOnRunning() {
        return isTimeOver() && totalDistance < goal.getDistance();
    }

    @JsonIgnore
    public boolean isCurrentTimeBeforeStartedAt() {
        LocalDateTime currentTime = now();
        return currentTime.isBefore(this.getGoal().getStartedAt());
    }

    @JsonIgnore
    public boolean isTeamFinishedRunning() {
        return isTimeOver() || this.totalDistance >= goal.getDistance();
    }

    public void participateRunning(Long memberId) {
        checkCanParticipate(memberId);
        this.getOnlineMembers().add(memberId);
    }

    public void leaveRunning(Long memberId) {
        this.getOnlineMembers().remove(memberId);
    }

    private void checkCanParticipate(Long memberId) {
        checkMemberIncludedInTeam(memberId);
        checkNowIsBetweenStartedAndRunningSeconds();
    }

    private void checkMemberIncludedInTeam(Long memberId) {
        this.getTotalMembers()
                .stream()
                .filter(id -> id == memberId)
                .findFirst().orElseThrow(MemberNotIncludedTeamException::new);
    }

    private void checkNowIsBetweenStartedAndRunningSeconds() {
        if (isTimeOver() || isCurrentTimeBeforeStartedAt())
            throw new CurrentIsNotRunningTimeException();
    }
}