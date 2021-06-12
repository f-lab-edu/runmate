package com.runmate.domain.redis;

import com.runmate.domain.running.Goal;
import com.runmate.exception.AdminNotIncludedException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import static java.time.LocalDateTime.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("running:team")
public class TeamInfo {
    @Id
    private long teamId;
    private List<Long> members = new ArrayList<>();
    private float totalDistance;
    private long adminId;

    @Builder
    public TeamInfo(long teamId, List<Long> members, long adminId) {
        checkAdminIncludeInMembers(members, adminId);

        this.teamId = teamId;
        this.members = members;
        this.adminId = adminId;
        this.totalDistance = 0;
    }

    private void checkAdminIncludeInMembers(List<Long> members, long adminId) {
        members.stream()
                .filter(id -> id == adminId)
                .findFirst()
                .orElseThrow(AdminNotIncludedException::new);
    }

    public float increaseTotalDistance(MemberInfo memberInfo) {
        this.totalDistance += memberInfo.getTotalDistance();
        return this.totalDistance;
    }

    public boolean isGoalSuccess(Goal goal) {
        LocalDateTime current = now();
        LocalDateTime endTime = goal.getStarted_at().plus(goal.getTotalRunningSeconds(), ChronoUnit.SECONDS);
        if (current.isEqual(endTime) || current.isAfter(endTime)) {
            return totalDistance >= goal.getTotalDistance();
        }
        return false;
    }
}