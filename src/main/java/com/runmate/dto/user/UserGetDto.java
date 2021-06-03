package com.runmate.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserGetDto {
    private Long id;
    private String email;
    private String username;
    private Region region;
    private String introduction;
    private Grade grade;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdAt;
}
