package com.runmate.repository.activity;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.runmate.dto.activity.ActivityDto;
import com.runmate.dto.activity.ActivityStatisticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.runmate.domain.activity.QActivity.*;
import static com.runmate.domain.activity.QActivity.activity;
import static com.runmate.domain.user.QUser.*;
import static com.runmate.domain.user.QUser.user;
import static com.runmate.repository.activity.ActivityRepository.*;

@Repository
@RequiredArgsConstructor
public class ActivityQueryRepository {
    private final JPAQueryFactory queryFactory;

    public ActivityStatisticsDto findAllByUserAndBetweenDates(String email, LocalDate fromDate, LocalDate toDate) {
        return queryFactory
                .select(getActivityStatisticDtoConstructor())
                .from(activity)
                .innerJoin(activity.user, user)
                .where(user.email.eq(email))
                .where(activity.createdAt.after(LocalDateTime.of(fromDate, LocalTime.of(0, 0))))
                .where(activity.createdAt.before(LocalDateTime.of(toDate, LocalTime.of(0, 0))))
                .groupBy(user)
                .fetchOne();
    }

    private ConstructorExpression<ActivityStatisticsDto> getActivityStatisticDtoConstructor() {
        return Projections.constructor(ActivityStatisticsDto.class,
                activity.count(),
                getSumDistance(),
                getSumSecondsOfRunningTime(),
                getSumSecondsOfRunningTime().divide(getSumDistance()),
                activity.calories.sum()
        );
    }

    public List<ActivityDto> findAllByUserWithPagination(String email, Pageable pageable) {
        return queryFactory
                .select(getActivityDtoConstructor())
                .from(activity)
                .innerJoin(activity.user, user)
                .where(user.email.eq(email))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private ConstructorExpression<ActivityDto> getActivityDtoConstructor() {
        return Projections.constructor(ActivityDto.class,
                activity.id,
                activity.distance,
                getSecondsOfRunningTime(),
                getSecondsOfRunningTime().divide(activity.distance),
                activity.calories,
                activity.createdAt
        );
    }
}