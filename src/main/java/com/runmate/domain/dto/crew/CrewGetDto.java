package com.runmate.domain.dto.crew;

import com.runmate.domain.activity.RunningTime;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CrewGetDto {
    private Long id;
    private String name;
    private float totalDistance;
    private LocalDateTime createdAt;

    public CrewGetDto(Long id, String name, float totalDistance, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.totalDistance = totalDistance;
        this.createdAt = createdAt;
    }
}
