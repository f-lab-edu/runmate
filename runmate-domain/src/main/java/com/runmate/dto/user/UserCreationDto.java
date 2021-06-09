package com.runmate.dto.user;

import lombok.*;
import com.runmate.domain.user.Region;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCreationDto {
    @Email(message = "Field is not email format")
    private String email;
    @NotNull(message = "Field can't be null value")
    private String password;
    @NotNull(message = "Field can't be null value")
    private String username;
    @NotNull(message = "Field can't be null value")
    private Region region;

    private String introduction;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public UserCreationDto(String email, String password, String username, Region region, String introduction) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.region = region;
        this.introduction = introduction;
    }
}
