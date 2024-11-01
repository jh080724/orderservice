package com.playdata.orderservice.common.entity;


import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass   // 테이블과 관련이 없고(=테이블로 생성이 안되고),
                    // 컬럼 정보만 자식에게 제공하기 위해 사용하는 어노테이션
public abstract class BaseTimeEntity {  // abstract 자식으로서만 생성

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}
