package com.runmate.repository.crew;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.runmate.domain.activity.QActivity;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.QCrew;
import com.runmate.domain.crew.QCrewUser;
import com.runmate.domain.dto.crew.CrewGetDto;
import com.runmate.domain.user.QUser;
import com.runmate.domain.user.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CrewQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<CrewGetDto> findByLocationWithSorted(Region region, int offset, int limit) {

        QActivity activity = QActivity.activity;
        QUser user = QUser.user;
        QCrewUser crewUser = QCrewUser.crewUser;
        QCrew crew = QCrew.crew;

        ConstructorExpression<CrewGetDto> crewGetDtoConstructor = Projections.constructor(CrewGetDto.class,
                crew.id, crew.name, activity.distance.sum(), crew.createdAt);

        return queryFactory.select(crewGetDtoConstructor)
                .from(activity)
                .innerJoin(activity.user, user)
                .innerJoin(user.crewUser, crewUser)
                .innerJoin(crewUser.crew, crew)
                .where(eqSi(region.getSi()), eqGu(region.getGu()), eqGun(region.getGun()))
                .groupBy(crew.id)
                .orderBy(activity.distance.sum().desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    private BooleanExpression eqSi(String si) {
        if (!StringUtils.hasText(si))
            return null;
        return QCrew.crew.region.si.eq(si);
    }

    private BooleanExpression eqGu(String gu) {
        if (!StringUtils.hasText(gu))
            return null;
        return QCrew.crew.region.gu.eq(gu);
    }

    private BooleanExpression eqGun(String gun) {
        if (!StringUtils.hasText(gun))
            return null;
        return QCrew.crew.region.gun.eq(gun);
    }
}
