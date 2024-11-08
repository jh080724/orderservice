package com.playdata.orderservice.ordering.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrdering is a Querydsl query type for Ordering
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrdering extends EntityPathBase<Ordering> {

    private static final long serialVersionUID = 247506665L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrdering ordering = new QOrdering("ordering");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<OrderDetail, QOrderDetail> orderDetails = this.<OrderDetail, QOrderDetail>createList("orderDetails", OrderDetail.class, QOrderDetail.class, PathInits.DIRECT2);

    public final EnumPath<OrderStatus> orderStatus = createEnum("orderStatus", OrderStatus.class);

    public final com.playdata.orderservice.user.entity.QUser user;

    public QOrdering(String variable) {
        this(Ordering.class, forVariable(variable), INITS);
    }

    public QOrdering(Path<? extends Ordering> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrdering(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrdering(PathMetadata metadata, PathInits inits) {
        this(Ordering.class, metadata, inits);
    }

    public QOrdering(Class<? extends Ordering> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.playdata.orderservice.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

