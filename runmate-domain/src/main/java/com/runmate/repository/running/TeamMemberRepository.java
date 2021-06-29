package com.runmate.repository.running;

import com.runmate.domain.running.Team;
import com.runmate.domain.running.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByIdAndTeam(Long id, Team team);
}
