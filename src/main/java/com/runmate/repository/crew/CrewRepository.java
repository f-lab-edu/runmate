package com.runmate.repository.crew;

import com.runmate.domain.crew.Crew;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CrewRepository extends JpaRepository<Crew, Long> {
}
