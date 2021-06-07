package com.runmate.domain.running;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "target_distance")
    private float targetDistance;

    @Column(name = "average_pace")
    private LocalTime averagePace;

    @Builder
    public Team(String title, float targetDistance, LocalTime averagePace) {
        this.title = title;
        this.targetDistance = targetDistance;
        this.averagePace = averagePace;
    }
}