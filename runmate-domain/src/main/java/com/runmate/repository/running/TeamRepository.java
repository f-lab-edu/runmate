package com.runmate.repository.running;

import com.runmate.domain.running.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("SELECT T FROM Team T join fetch T.teamMembers where T.id =:teamId")
    Optional<Team> findByIdHaveTeamMembers(@Param("teamId") Long teamId);
}