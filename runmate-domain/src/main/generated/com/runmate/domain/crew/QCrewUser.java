package com.runmate.domain.crew;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrewUser is a Querydsl query type for CrewUser
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCrewUser extends EntityPathBase<CrewUser> {

    private static final long serialVersionUID = 324595610L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrewUser crewUser = new QCrewUser("crewUser");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QCrew crew;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final com.runmate.domain.user.QUser user;

    public QCrewUser(String variable) {
        this(CrewUser.class, forVariable(variable), INITS);
    }

    public QCrewUser(Path<? extends CrewUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCrewUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCrewUser(PathMetadata metadata, PathInits inits) {
        this(CrewUser.class, metadata, inits);
    }

    public QCrewUser(Class<? extends CrewUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crew = inits.isInitialized("crew") ? new QCrew(forProperty("crew"), inits.get("crew")) : null;
        this.user = inits.isInitialized("user") ? new com.runmate.domain.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

