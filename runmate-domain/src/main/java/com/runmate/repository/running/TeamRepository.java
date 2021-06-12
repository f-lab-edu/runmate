package com.runmate.repository.running;

import com.runmate.domain.running.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
