package com.runmate.domain.activity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runmate.domain.common.LocalDateTimeConverter;
import com.runmate.domain.common.LocalTimeConverter;
import com.runmate.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "activity")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Activity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "distance", nullable = false)
    private float distance;

    @Column(name = "running_time", nullable = false)
    @Convert(converter = LocalTimeConverter.class)
    @JsonFormat(pattern = "HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalTime runningTime;

    @Column(name = "calories")
    private int calories;

    @Column(name = "created_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    @Builder
    public Activity(float distance, LocalTime runningTime, int calories, LocalDateTime createdAt) {
        this.distance = distance;
        this.runningTime = runningTime;
        this.calories = calories;
        this.createdAt = createdAt;
    }
}
