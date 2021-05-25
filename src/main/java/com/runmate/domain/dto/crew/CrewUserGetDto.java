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
    private float totalDistance;
    private Role role;
    private String username;

    public CrewUserGetDto(Long id, float totalDistance,Role role,String username) {
        this.id = id;
        this.totalDistance = totalDistance;
        this.role=role;
        this.username=username;
    }
}
