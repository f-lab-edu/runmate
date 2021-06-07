package com.runmate.dto.crew;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CrewJoinRequestGetDto {
    private final Long id;
    private final String email;
    private final String name;
    private final Region location;
    private final Grade grade;
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public CrewJoinRequestGetDto(Long id, User user, LocalDateTime createdAt) {
        this.id = id;
        this.email = user.getEmail();
        this.name = user.getUsername();
        this.location = user.getRegion();
        this.grade = user.getGrade();
        this.createdAt = createdAt;
    }
}
