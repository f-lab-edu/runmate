package com.runmate.repository.running;

import com.runmate.domain.running.TeamMember;
import com.runmate.dto.running.TeamMemberCreationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    @Query("SELECT new com.runmate.dto.running.TeamMemberCreationResponse(tm.id, u.name, u.email, u.grade) " +
            "FROM TeamMember tm " +
            "JOIN FETCH com.runmate.domain.crew.CrewUser cu " +
            "JOIN FETCH com.runmate.domain.user.User u " +
            "WHERE tm.id = :teamMemberId")
    Optional<TeamMemberCreationResponse> findWithUserById(long teamMemberId);
}
