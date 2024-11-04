package com.playdata.orderservice.ordering.entity;

import com.playdata.orderservice.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
public class Ordering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;  // 누가 주문했는지

    private OrderStatus orderStatus;

    // mappedBy: 없는 타입 필드 맴핑
    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST)
    private List<OrderDetail> orderDetails;  // 주문 상세 리스트

}
