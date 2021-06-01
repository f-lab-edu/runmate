package com.runmate.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1983729297L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final ListPath<com.runmate.domain.activity.Activity, com.runmate.domain.activity.QActivity> activities = this.<com.runmate.domain.activity.Activity, com.runmate.domain.activity.QActivity>createList("activities", com.runmate.domain.activity.Activity.class, com.runmate.domain.activity.QActivity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.runmate.domain.crew.QCrewUser crewUser;

    public final StringPath email = createString("email");

    public final EnumPath<Grade> grade = createEnum("grade", Grade.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath introduction = createString("introduction");

    public final ListPath<com.runmate.domain.crew.CrewJoinRequest, com.runmate.domain.crew.QCrewJoinRequest> joinRequests = this.<com.runmate.domain.crew.CrewJoinRequest, com.runmate.domain.crew.QCrewJoinRequest>createList("joinRequests", com.runmate.domain.crew.CrewJoinRequest.class, com.runmate.domain.crew.QCrewJoinRequest.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final QRegion region;

    public final StringPath username = createString("username");

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crewUser = inits.isInitialized("crewUser") ? new com.runmate.domain.crew.QCrewUser(forProperty("crewUser"), inits.get("crewUser")) : null;
        this.region = inits.isInitialized("region") ? new QRegion(forProperty("region")) : null;
    }

}

