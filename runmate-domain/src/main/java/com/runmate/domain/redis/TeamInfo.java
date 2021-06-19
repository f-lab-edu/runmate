package com.runmate.domain.redis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.runmate.exception.AdminNotIncludedException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("running:team")
public class TeamInfo {
    @Id
    private long teamId;
    private List<Long> members = new ArrayList<>();
    private float totalDistance;
    private long adminId;
    private GoalForTempStore goal;
    private long runningSeconds;

    @Builder
    public TeamInfo(long teamId, List<Long> members, long adminId, GoalForTempStore goal) {
        checkAdminIncludeInMembers(members, adminId);

        this.teamId = teamId;
        this.members = members;
        this.adminId = adminId;
        this.goal = goal;
        this.totalDistance = 0;
        this.runningSeconds = 0;
    }

    private void checkAdminIncludeInMembers(List<Long> members, long adminId) {
        members.stream()
                .filter(id -> id == adminId)
                .findFirst()
                .orElseThrow(AdminNotIncludedException::new);
    }

    public float increaseTotalDistance(float distance) {
        this.totalDistance += distance;
        updateRunningSeconds();
        return this.totalDistance;
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
        return goal.getRunningSeconds() < this.runningSeconds;
    }
    @JsonIgnore
    public boolean isFailOnRunning() {
        return isTimeOver() && totalDistance < goal.getDistance();
    }
}