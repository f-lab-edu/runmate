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
    @Email(message = "Field must be a email format")
    private String email;

    @NotBlank(message = "Field can't be blank")
    private String name;

    private String description;

    @NotNull
    private Region region;

    private Grade gradeLimit;

    @Builder
    public CrewPostDto(String email, String name, String description, Region region, Grade gradeLimit) {
        this.email = email;
        this.name = name;
        this.description = description;
        this.region = region;
        this.gradeLimit = gradeLimit;
    }
}
