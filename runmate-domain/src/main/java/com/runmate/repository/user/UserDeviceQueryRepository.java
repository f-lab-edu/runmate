package com.runmate.repository.user;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.runmate.domain.user.QUserDevice.userDevice;
import static com.runmate.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserDeviceQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<String> findAllDeviceTokenByEmail(String email) {
        return queryFactory.select(
                    Projections.constructor(String.class, userDevice.deviceToken)
                )
                .from(user)
                .leftJoin(userDevice)
                .on(user.id.eq(userDevice.user.id))
                .where(user.email.eq(email))
                .fetch();
    }
}
