package com.runmate.domain.running;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QTeam is a Querydsl query type for Team
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTeam extends EntityPathBase<Team> {

    private static final long serialVersionUID = -1280202645L;

    public static final QTeam team = new QTeam("team");

    public final TimePath<java.time.LocalTime> averagePace = createTime("averagePace", java.time.LocalTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Float> targetDistance = createNumber("targetDistance", Float.class);

    public final StringPath title = createString("title");

    public QTeam(String variable) {
        super(Team.class, forVariable(variable));
    }

    public QTeam(Path<? extends Team> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeam(PathMetadata metadata) {
        super(Team.class, metadata);
    }

}

