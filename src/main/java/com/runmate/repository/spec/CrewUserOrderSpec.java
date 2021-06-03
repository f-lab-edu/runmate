package com.runmate.repository.spec;

import com.querydsl.core.types.OrderSpecifier;
import lombok.Getter;

import java.util.Arrays;

import static com.runmate.domain.crew.QCrewUser.crewUser;
import static com.runmate.repository.activity.ActivityRepository.getSumDistance;
import static com.runmate.repository.activity.ActivityRepository.getSumSecondsOfRunningTime;
import static com.runmate.repository.crew.CrewUserQueryRepository.*;

@Getter
public enum CrewUserOrderSpec {

    ASC_CREATED_AT(true, "created_at", crewUser.createdAt.asc()),
    ASC_RUNNING_TIME(true, "running_time", getSumSecondsOfRunningTime().asc()),
    ASC_DISTANCE(true, "distance", getSumDistance().asc()),

    DESC_CREATED_AT(false, "created_at", crewUser.createdAt.desc()),
    DESC_RUNNING_TIME(false, "running_time", getSumSecondsOfRunningTime().desc()),
    DESC_DISTANCE(false, "distance", getSumDistance().desc());

    private boolean isAscending;
    private String property;
    private OrderSpecifier specifier;

    CrewUserOrderSpec(boolean isAscending, String property, OrderSpecifier specifier) {
        this.isAscending = isAscending;
        this.property = property;
        this.specifier = specifier;
    }

    public static CrewUserOrderSpec of(boolean isAscending, String sortBy) {
        return Arrays.stream(CrewUserOrderSpec.values())
                .filter(crewUserOrderSpec -> crewUserOrderSpec.isAscending == isAscending)
                .filter(crewUserOrderSpec -> crewUserOrderSpec.property.equals(sortBy))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
