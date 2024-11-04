package com.playdata.orderservice.ordering.entity;

import com.playdata.orderservice.ordering.dto.OrderingListResDto;
import com.playdata.orderservice.user.entity.Role;
import com.playdata.orderservice.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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


    @Enumerated(EnumType.STRING)  // 상수이름을 스트링으로 DB에 저장
    @Builder.Default    // Builder로 build시에 초기화된 값으로 세팅하기 위한 어노테이션
    private OrderStatus orderStatus = OrderStatus.ORDERED;

    // mappedBy: 없는 타입 필드 맴핑
    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST)
    private List<OrderDetail> orderDetails;  // 주문 상세 리스트

    public OrderingListResDto fromEntity(){
        // DB에서 조회해 온 Ordering에서 상세내역을 확인
        List<OrderDetail> orderDetailList = this.getOrderDetails();
        List<OrderingListResDto.OrderDetailDto> orderDetailDtos = new ArrayList<>();

        // OrderDetail 엔터티를 OrderDetailDto로 변환
        // 변환 후에는 리스트에 추가
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetailDtos.add(orderDetail.fromEntity());
        }

        // 주문 상세내역dto 포장이 완료되면 하나의 주문내역 자체를 dto로 변환해서 리턴.
        return OrderingListResDto.builder()
                .id(this.id)
                .userEmail(this.user.getEmail())
                .orderStatus(this.orderStatus)
                .orderDetails(orderDetailDtos)
                .build();
    }

    public void updateStatus(OrderStatus status){
        this.orderStatus = status;
    }
}
