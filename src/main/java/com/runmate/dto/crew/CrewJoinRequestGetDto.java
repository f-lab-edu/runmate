package com.runmate.dto.crew;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CrewJoinRequestGetDto {
    private Long id;
    private Long crewId;
    private String email;
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public CrewJoinRequestGetDto(Long id, Crew crew, User user, LocalDateTime createdAt) {
        this.id = id;
        this.crewId = crew.getId();
        this.email = user.getEmail();
        this.createdAt = createdAt;
    }
}
