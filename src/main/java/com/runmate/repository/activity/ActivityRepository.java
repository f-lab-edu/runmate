package com.runmate.repository.activity;

import com.runmate.domain.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT a FROM Activity a JOIN User u " +
            "ON a.user.id = u.id " +
            "WHERE u.id = :userId " +
            "AND a.createdAt >= :from AND a.createdAt <= :to")
    List<Activity> findAllByUserAndBetweenDates(Long userId, LocalDateTime from, LocalDateTime to);
}
