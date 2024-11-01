package com.playdata.orderservice.user.entity;

import com.playdata.orderservice.common.entity.Address;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(unique = true, length = 20, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded   // @Embeddable로 선언된 값을 대입(기본 생성자 필수, @NoArg... 있어야함.)
    private Address address;

    @Enumerated(EnumType.STRING)  // 상수이름을 스트링으로 DB에 저장
    @Builder.Default    // Builder로 build시에 초기화된 값으로 세팅하기 위한 어노테이션
    private Role role = Role.USER;

}
