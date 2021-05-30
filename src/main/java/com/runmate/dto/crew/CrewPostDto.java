package com.runmate.dto.crew;

import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewPostDto {
    @Email
    private String email;

    @NotNull
    private CrewPostData data;

    @Builder
    public CrewPostDto(@Email String email, @NotNull CrewPostData data) {
        this.email = email;
        this.data = data;
    }
}

@Getter
class CrewPostData {
    @NotBlank(message = "Field can't be blank")
    private final String name;

    private final String description;

    @NotNull
    private final Region region;

    @NotNull
    private final Grade gradeLimit;

    @Builder
    public CrewPostData(String name, String description, Region region, Grade gradeLimit) {
        this.name = name;
        this.description = description;
        this.region = region;
        this.gradeLimit = gradeLimit;
    }
}
