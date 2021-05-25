package com.runmate.domain.dto.crew;

import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewPostDto {
    private String name;
    private String description;
    private Region region;
    private Grade gradeLimit;
}
