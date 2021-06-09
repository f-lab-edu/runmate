package com.runmate.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QRegion is a Querydsl query type for Region
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QRegion extends BeanPath<Region> {

    private static final long serialVersionUID = 502862744L;

    public static final QRegion region = new QRegion("region");

    public final StringPath gu = createString("gu");

    public final StringPath gun = createString("gun");

    public final StringPath si = createString("si");

    public QRegion(String variable) {
        super(Region.class, forVariable(variable));
    }

    public QRegion(Path<? extends Region> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRegion(PathMetadata metadata) {
        super(Region.class, metadata);
    }

}

