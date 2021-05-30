package com.runmate.repository.crew;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrewUserRepository extends JpaRepository<CrewUser,Long> {
    Optional<CrewUser> findByCrewAndUser(Crew crew, User user);
    Optional<CrewUser> findByUser(User user);
    List<CrewUser> findAllByCrew(Crew crew);
}
