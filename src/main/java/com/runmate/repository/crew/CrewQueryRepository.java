package com.runmate.repository.crew;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.runmate.domain.crew.QCrew;
import com.runmate.domain.dto.crew.CrewGetDto;
import com.runmate.domain.user.Region;
import com.runmate.repository.spec.CrewOrderSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.runmate.domain.activity.QActivity.activity;
import static com.runmate.domain.user.QUser.user;
import static com.runmate.domain.crew.QCrew.crew;
import static com.runmate.domain.crew.QCrewUser.crewUser;
import static com.runmate.repository.activity.ActivityRepository.getSumDistance;
import static com.runmate.repository.activity.ActivityRepository.getSumSecondsOfRunningTime;

@RequiredArgsConstructor
@Repository
public class CrewQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<CrewGetDto> findByLocationWithSorted(Region region, Pageable pageable, CrewOrderSpec orderSpec) {
        return queryFactory.select(getCrewGetDtoConstructor())
                .from(activity)
                .innerJoin(activity.user, user)
                .innerJoin(user.crewUser, crewUser)
                .innerJoin(crewUser.crew, crew)
                .where(eqSi(region.getSi()), eqGu(region.getGu()), eqGun(region.getGun()))
                .groupBy(crew.id)
                .orderBy(orderSpec.getSpecifier())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private ConstructorExpression<CrewGetDto> getCrewGetDtoConstructor() {
        return Projections.constructor(CrewGetDto.class,
                crew.id,
                crew.name,
                getSumDistance(),
                getSumSecondsOfRunningTime(),
                crew.createdAt);
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
