package com.runmate.repository.running;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.runmate.dto.running.TeamMemberCreationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.runmate.domain.running.QTeamMember.teamMember;
import static com.runmate.domain.user.QUser.user;
import static com.runmate.domain.crew.QCrewUser.crewUser;

@RequiredArgsConstructor
@Repository
public class TeamMemberQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<TeamMemberCreationResponse> findByIdWithUser(long teamMemberId) {
        return Optional.ofNullable(queryFactory.select(
                Projections.constructor(TeamMemberCreationResponse.class,
                    teamMember.id,
                    user.username,
                    user.email,
                    user.grade
                ))
                .from(teamMember)
                .innerJoin(crewUser)
                .on(teamMember.crewUser.id.eq(crewUser.id))
                .innerJoin(user)
                .on(crewUser.user.id.eq(user.id))
                .where(teamMember.id.eq(teamMemberId))
                .fetchOne());
    }
}
