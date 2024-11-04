package com.playdata.orderservice.ordering.dto;

import com.playdata.orderservice.ordering.entity.OrderStatus;
import lombok.*;

import java.util.List;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderingListResDto {
    /*
    * 하나의 주문에 대한 내용
        id: 주문번호,
        email: 주문한 사람 email,
        orderStatus: 주문상태
         - List<OrderDetailDto>
     */
    private Long id;
    private String userEmail;
    private OrderStatus orderStatus;
    private List<OrderDetailDto> orderDetails;

    @Getter @Setter @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailDto {
    /*
    id: 주문상세 번호,
    productName: 상품명
    count: 수량
     */
        private Long id;
        private String productName;
        private int count;
    }
}
