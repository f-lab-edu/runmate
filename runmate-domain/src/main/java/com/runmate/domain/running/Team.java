package com.runmate.domain.running;

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

    @Column(name = "title")
    private String title;

    @Embedded
    private Goal goal;

    @Embedded
    private Result result;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<TeamMember> teamMembers = new ArrayList<>();

    @Builder
    public Team(String title, Goal goal) {
        this.title = title;
        this.goal = goal;
        this.result = Result.builder()
                .totalDistance(0F)
                .status(CompleteStatus.FAIL)
                .totalRunningSeconds(0L)
                .build();
    }

    public void decideResult(float distance, long runningSeconds, boolean isSuccess) {
        this.result = Result.builder()
                .totalDistance(distance)
                .totalRunningSeconds(runningSeconds)
                .status(CompleteStatus.of(isSuccess))
                .build();

    }
}