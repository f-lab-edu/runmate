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
public class CrewCreationDto {
    @Email
    private String email;

    @NotNull
    private CrewCreationData data;

    @Builder
    public CrewCreationDto(@Email String email, @NotNull CrewCreationData data) {
        this.email = email;
        this.data = data;
    }
}

@Getter
class CrewCreationData {
    @NotBlank(message = "Field can't be blank")
    private final String name;

    private final String description;

    @NotNull
    private final Region region;

    @NotNull
    private final Grade gradeLimit;

    @Builder
    public CrewCreationData(String name, String description, Region region, Grade gradeLimit) {
        this.name = name;
        this.description = description;
        this.region = region;
        this.gradeLimit = gradeLimit;
    }
}
