package com.runmate.repository.crew;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.runmate.domain.activity.Activity;
import com.runmate.domain.activity.QActivity;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.QCrew;
import com.runmate.domain.crew.QCrewUser;
import com.runmate.domain.dto.crew.CrewUserGetDto;
import com.runmate.domain.user.QUser;
import com.runmate.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CrewUserQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<CrewUserGetDto> findCrewUserOrderByActivity(Long crewId, int offset, int limit) {
        QActivity activity = QActivity.activity;
        QUser user = QUser.user;
        QCrewUser crewUser = QCrewUser.crewUser;
        QCrew crew = QCrew.crew;

        ConstructorExpression<CrewUserGetDto> crewUserGetDtoConstructor = Projections.constructor(CrewUserGetDto.class,
                crewUser.id, activity.distance.sum(), crewUser.role, crewUser.user.username, crewUser.createdAt);

        return queryFactory.select(crewUserGetDtoConstructor)
                .from(activity)
                .innerJoin(activity.user, user)
                .innerJoin(user.crewUser, crewUser)
                .innerJoin(crewUser.crew, crew)
                .where(crew.id.eq(crewId))
                .groupBy(crewUser)
                .orderBy(activity.distance.sum().desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }
}
