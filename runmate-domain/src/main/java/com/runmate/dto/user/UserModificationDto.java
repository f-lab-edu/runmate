package com.runmate.dto.user;

import lombok.*;
import com.runmate.domain.user.Region;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserModificationDto {
    @Id
    @NotNull(message = "Field can't be null value")
    private Long id;

    private String password;

    @NotNull(message = "Field can't be null value")
    private String username;

    @NotNull(message = "Field can't be null value")
    private Region region;

    private String introduction;

    @Builder
    public UserModificationDto(Long id, String password, String username, Region region, String introduction) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.region = region;
        this.introduction = introduction;
    }
}
