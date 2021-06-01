package com.runmate.repository.crew;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.runmate.dto.crew.CrewUserGetDto;
import com.runmate.repository.spec.CrewUserOrderSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.runmate.domain.activity.QActivity.activity;
import static com.runmate.domain.crew.QCrew.crew;
import static com.runmate.domain.crew.QCrewUser.crewUser;
import static com.runmate.domain.user.QUser.user;
import static com.runmate.repository.activity.ActivityRepository.getSumDistance;
import static com.runmate.repository.activity.ActivityRepository.getSumSecondsOfRunningTime;

@Repository
@RequiredArgsConstructor
public class CrewUserQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<CrewUserGetDto> findCrewUserWithSorted(Long crewId, Pageable pageable, CrewUserOrderSpec orderSpec) {
        return queryFactory.select(getCrewUserGetDtoConstructorExpression())
                .from(activity)
                .innerJoin(activity.user, user)
                .innerJoin(user.crewUser, crewUser)
                .innerJoin(crewUser.crew, crew)
                .where(crew.id.eq(crewId))
                .groupBy(crewUser)
                .orderBy(orderSpec.getSpecifier())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private ConstructorExpression<CrewUserGetDto> getCrewUserGetDtoConstructorExpression() {
        return Projections.constructor(CrewUserGetDto.class,
                crewUser.id,
                getSumDistance(),
                crewUser.role,
                crewUser.user.username,
                crewUser.createdAt,
                getSumSecondsOfRunningTime());
    }
}
