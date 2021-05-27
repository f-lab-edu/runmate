package com.runmate.repository.spec;

import com.querydsl.core.types.OrderSpecifier;

import java.util.Arrays;

import static com.runmate.domain.crew.QCrew.crew;
import static com.runmate.repository.activity.ActivityRepository.getSumDistance;
import static com.runmate.repository.activity.ActivityRepository.getSumSecondsOfRunningTime;

public enum CrewOrderSpec {
    ASC_CREATED_AT("ASC", "createdAt", crew.createdAt.asc()),
    ASC_RUNNING_TIME("ASC", "runningTime", getSumSecondsOfRunningTime().asc()),
    ASC_DISTANCE("ASC", "distance", getSumDistance().asc()),

    DESC_CREATED_AT("DESC", "createdAt", crew.createdAt.desc()),
    DESC_RUNNING_TIME("DESC", "runningTime", getSumSecondsOfRunningTime().desc()),
    DESC_DISTANCE("DESC", "distance", getSumDistance().desc());

    private OrderSpecifier orderSpec;
    private String direction;
    private String property;

    CrewOrderSpec(String direction, String property, OrderSpecifier orderSpec) {
        this.direction = direction;
        this.property = property;
        this.orderSpec = orderSpec;
    }

    public OrderSpecifier getSpecifier() {
        return this.orderSpec;
    }

    public String getDirection() {
        return this.direction;
    }

    public String getProperty() {
        return this.property;
    }

    public CrewOrderSpec of(String direction, String property) {
        return Arrays.stream(CrewOrderSpec.values())
                .filter(orderSpec -> orderSpec.getDirection().equals(direction) &&
                        orderSpec.getProperty().equals(property))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
