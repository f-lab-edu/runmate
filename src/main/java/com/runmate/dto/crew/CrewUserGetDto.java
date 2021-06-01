package com.runmate.dto.crew;

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
    private String username;
    private Role role;
    private float totalDistance;
    private Long totalRunningSeconds;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdAt;

    public CrewUserGetDto(Long id, String username, Role role, float totalDistance, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.totalDistance = totalDistance;
        this.createdAt = createdAt;
    }

    public CrewUserGetDto(Long id, String username, Role role, float totalDistance, Long totalRunningSeconds, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.totalDistance = totalDistance;
        this.totalRunningSeconds = totalRunningSeconds;
        this.createdAt = createdAt;
    }
}
