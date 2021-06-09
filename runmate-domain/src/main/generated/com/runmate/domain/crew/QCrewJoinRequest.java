package com.runmate.domain.crew;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrewJoinRequest is a Querydsl query type for CrewJoinRequest
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCrewJoinRequest extends EntityPathBase<CrewJoinRequest> {

    private static final long serialVersionUID = -820228266L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrewJoinRequest crewJoinRequest = new QCrewJoinRequest("crewJoinRequest");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QCrew crew;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.runmate.domain.user.QUser user;

    public QCrewJoinRequest(String variable) {
        this(CrewJoinRequest.class, forVariable(variable), INITS);
    }

    public QCrewJoinRequest(Path<? extends CrewJoinRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCrewJoinRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCrewJoinRequest(PathMetadata metadata, PathInits inits) {
        this(CrewJoinRequest.class, metadata, inits);
    }

    public QCrewJoinRequest(Class<? extends CrewJoinRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crew = inits.isInitialized("crew") ? new QCrew(forProperty("crew"), inits.get("crew")) : null;
        this.user = inits.isInitialized("user") ? new com.runmate.domain.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

