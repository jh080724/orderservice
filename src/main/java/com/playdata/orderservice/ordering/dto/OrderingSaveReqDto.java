package com.playdata.orderservice.ordering.dto;

import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderingSaveReqDto {

    // 토큰에서 사용자를 식별할 수 있기 때문에 userId 같은 것은 받을 필요가 없다.
    private Long productId;   // 상품번호
    private int productCount; // 주문수량

}
