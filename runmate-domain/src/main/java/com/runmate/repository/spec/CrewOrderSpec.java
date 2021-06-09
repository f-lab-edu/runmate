package com.runmate.repository.spec;

import com.querydsl.core.types.OrderSpecifier;
import lombok.Getter;

import java.util.Arrays;

import static com.runmate.domain.crew.QCrew.crew;
import static com.runmate.repository.activity.ActivityRepository.getSumDistance;
import static com.runmate.repository.activity.ActivityRepository.getSumSecondsOfRunningTime;

@Getter
public enum CrewOrderSpec {
    ASC_CREATED_AT(true, "created_at", crew.createdAt.asc()),
    ASC_RUNNING_TIME(true, "running_time", getSumSecondsOfRunningTime().asc()),
    ASC_DISTANCE(true, "distance", getSumDistance().asc()),

    DESC_CREATED_AT(false, "created_at", crew.createdAt.desc()),
    DESC_RUNNING_TIME(false, "running_time", getSumSecondsOfRunningTime().desc()),
    DESC_DISTANCE(false, "distance", getSumDistance().desc());

    private final boolean isAscending;
    private final String property;
    private final OrderSpecifier specifier;

    CrewOrderSpec(boolean isAscending, String property, OrderSpecifier specifier) {
        this.isAscending = isAscending;
        this.property = property;
        this.specifier = specifier;
    }

    public static CrewOrderSpec of(boolean isAscending, String sortBy) {
        return Arrays.stream(values())
                .filter(crewOrderSpec -> crewOrderSpec.isAscending == isAscending)
                .filter(crewOrderSpec -> crewOrderSpec.property.equals(sortBy))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("invalid sorting parameter"));
    }
}
