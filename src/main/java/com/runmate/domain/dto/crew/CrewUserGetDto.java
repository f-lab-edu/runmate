package com.runmate.domain.dto.crew;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runmate.domain.crew.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CrewUserGetDto {
    private Long id;
    private float totalDistance;
    private Role role;
    private String username;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdAt;
    private Long totalRunningSeconds;

    public CrewUserGetDto(Long id, float totalDistance, Role role, String username, LocalDateTime createdAt) {
        this.id = id;
        this.totalDistance = totalDistance;
        this.role = role;
        this.username = username;
        this.createdAt = createdAt;
    }

    public CrewUserGetDto(Long id, float totalDistance, Role role, String username, LocalDateTime createdAt, Long totalRunningSeconds) {
        this.id = id;
        this.totalDistance = totalDistance;
        this.role = role;
        this.username = username;
        this.createdAt = createdAt;
        this.totalRunningSeconds = totalRunningSeconds;
    }
}
