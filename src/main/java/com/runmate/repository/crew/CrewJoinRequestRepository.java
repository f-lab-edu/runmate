package com.runmate.repository.crew;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrewJoinRequestRepository extends JpaRepository<CrewJoinRequest, Long> {
    @Query("SELECT CJR FROM CrewJoinRequest CJR where CJR.crew =:crew")
    List<CrewJoinRequest> findAllByCrewWithPageable(@Param("crew") Crew crew, Pageable request);
}
