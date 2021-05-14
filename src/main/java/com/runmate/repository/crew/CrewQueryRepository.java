package com.runmate.repository.crew;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.QCrew;
import com.runmate.domain.user.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CrewQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<Crew> findByLocationWithSorted(Region region) {//paging,Sort condition 2가지 붙는다.
        QCrew crew = QCrew.crew;
        return queryFactory.
                select(crew).
                from(crew).
                where(eqSi(region.getSi()), eqGu(region.getGu()), eqGun(region.getGun())).fetch();
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
