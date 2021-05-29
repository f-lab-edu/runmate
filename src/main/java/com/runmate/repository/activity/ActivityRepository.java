package com.runmate.repository.activity;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.runmate.domain.activity.Activity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

import static com.runmate.domain.activity.QActivity.activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT a FROM Activity a JOIN User u " +
            "ON a.user.id = u.id " +
            "WHERE u.id = :userId " +
            "AND a.createdAt >= :from AND a.createdAt <= :to")
    List<Activity> findAllByUserAndBetweenDates(Long userId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT a FROM Activity a JOIN User u " +
            "ON a.user.id = u.id " +
            "WHERE u.id = :userId")
    List<Activity> findAllByUserWithPagination(Long userId, Pageable pageable);

    public static NumberExpression<Long> getSumSecondsOfRunningTime() {
        return Expressions.numberTemplate(Long.class, "function('TIME_TO_SEC',{0})", activity.runningTime).sum();
    }

    public static NumberExpression<Float> getSumDistance() {
        return activity.distance.sum();
    }
}
