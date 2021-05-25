package com.runmate.domain.dto.crew;

import com.runmate.domain.crew.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CrewUserGetDto {
    private Long id;
    private String name;
    private float totalDistance;
    private LocalDateTime createdAt;

    public CrewUserGetDto(Long id, String name, float totalDistance, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.totalDistance = totalDistance;
        this.createdAt = createdAt;
    }
}
