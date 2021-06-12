package com.runmate.domain.running;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Team(String title, Goal goal, Result result) {
        this.title = title;
        this.goal = goal;
        this.result = result;
    }
}