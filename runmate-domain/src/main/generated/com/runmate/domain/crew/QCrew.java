package com.runmate.domain.crew;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrew is a Querydsl query type for Crew
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCrew extends EntityPathBase<Crew> {

    private static final long serialVersionUID = -1114521809L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrew crew = new QCrew("crew");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ListPath<CrewUser, QCrewUser> crewUsers = this.<CrewUser, QCrewUser>createList("crewUsers", CrewUser.class, QCrewUser.class, PathInits.DIRECT2);

    public final StringPath description = createString("description");

    public final EnumPath<com.runmate.domain.user.Grade> gradeLimit = createEnum("gradeLimit", com.runmate.domain.user.Grade.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<CrewJoinRequest, QCrewJoinRequest> joinRequests = this.<CrewJoinRequest, QCrewJoinRequest>createList("joinRequests", CrewJoinRequest.class, QCrewJoinRequest.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final com.runmate.domain.user.QRegion region;

    public QCrew(String variable) {
        this(Crew.class, forVariable(variable), INITS);
    }

    public QCrew(Path<? extends Crew> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCrew(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCrew(PathMetadata metadata, PathInits inits) {
        this(Crew.class, metadata, inits);
    }

    public QCrew(Class<? extends Crew> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.region = inits.isInitialized("region") ? new com.runmate.domain.user.QRegion(forProperty("region")) : null;
    }

}

