package com.runmate.websocket.domain;

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

    @Column(name = "target_pace")
    private LocalTime targetPace;

    @Builder
    public Team(Long id, String title, float targetDistance, LocalTime targetPace) {
        this.id = id;
        this.title = title;
        this.targetDistance = targetDistance;
        this.targetPace = targetPace;
    }
}