package com.runmate.dto.crew;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CrewGetDto {
    private Long id;
    private String name;
    private float totalDistance;
    private Long totalRunningSeconds;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdAt;

    public CrewGetDto(Long id, String name, float totalDistance, Long totalRunningSeconds, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.totalDistance = totalDistance;
        this.totalRunningSeconds=totalRunningSeconds;
        this.createdAt = createdAt;
    }
}
