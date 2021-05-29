package com.runmate.repository.spec;

import com.querydsl.core.types.OrderSpecifier;

import java.util.Arrays;

import static com.runmate.domain.crew.QCrewUser.crewUser;
import static com.runmate.repository.activity.ActivityRepository.getSumDistance;
import static com.runmate.repository.activity.ActivityRepository.getSumSecondsOfRunningTime;
import static com.runmate.repository.crew.CrewUserQueryRepository.*;

public enum CrewUserOrderSpec {

    ASC_CREATED_AT("ASC", "createdAt", crewUser.createdAt.asc()),
    ASC_RUNNING_TIME("ASC", "runningTime", getSumSecondsOfRunningTime().asc()),
    ASC_DISTANCE("ASC", "distance", getSumDistance().asc()),

    DESC_CREATED_AT("DESC", "createdAt", crewUser.createdAt.desc()),
    DESC_RUNNING_TIME("DESC", "runningTime", getSumSecondsOfRunningTime().desc()),
    DESC_DISTANCE("DESC", "distance", getSumDistance().desc());

    private OrderSpecifier orderSpec;
    private String direction;
    private String property;

    CrewUserOrderSpec(String direction, String property, OrderSpecifier orderSpec) {
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

    public CrewUserOrderSpec of(String direction, String property) {
        return Arrays.stream(CrewUserOrderSpec.values())
                .filter(orderSpec -> orderSpec.getDirection().equals(direction) &&
                        orderSpec.getProperty().equals(property))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
