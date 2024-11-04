package com.playdata.orderservice.ordering.service;

import com.playdata.orderservice.common.auth.TokenUserInfo;
import com.playdata.orderservice.ordering.dto.OrderingListResDto;
import com.playdata.orderservice.ordering.dto.OrderingSaveReqDto;
import com.playdata.orderservice.ordering.entity.OrderDetail;
import com.playdata.orderservice.ordering.entity.OrderStatus;
import com.playdata.orderservice.ordering.entity.Ordering;
import com.playdata.orderservice.ordering.repository.OrderingRepository;
import com.playdata.orderservice.product.entity.Product;
import com.playdata.orderservice.product.repository.ProductRepository;
import com.playdata.orderservice.user.entity.User;
import com.playdata.orderservice.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderingService {

    private final OrderingRepository orderingRepository; // 의존성 주입
    private final UserRepository userRepository;    // 의존성 주입
    private final ProductRepository productRepository;  // 의존성 주입

    public Ordering createOrdering(List<OrderingSaveReqDto> dtoList, TokenUserInfo userInfo) {

        // Ordering(주문)객체를 생성하기 위해 회원정보를 얻어오기
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        // Ordering(주문)객체 생성
        Ordering ordering = Ordering.builder()
                .user(user)
                .orderDetails(new ArrayList<>()) // 아직 주문 상세 들어가기 전
                .build();

        // 주문 상세 내역에 대한 처리를 반복문으로 처리
        for (OrderingSaveReqDto dto : dtoList) {
            // dto에는 상품 고유 id가 있으니까 그걸 활용해서
            // product 객체를 조회.
            Product product =
                    productRepository.findById(dto.getProductId()).orElseThrow(
                    () -> new EntityNotFoundException("Product not found")
            );

            // 재고가 넉넉하게 있는지 확인.
            int quantity = dto.getProductCount();  // 주문개수(상품의 개수가 주문수량 보다 적을때)
            if(product.getStockQuantity() < quantity){
                throw new IllegalArgumentException("재고 부족!!!");
            }

            // 재고가 부족하지 않다면 재고 수량을 주문 수량만큼 빼주자.
            product.updateStockQuantity(quantity);

            // 주문상세 내역 엔터티를 생성한다.
            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .ordering(ordering)
                    .quantity(quantity)
                    .build();

            //주문내역 리스트에 상세 내역을 add하기.
            // CascadeType.PERSIST(영속컨택스트)로 세팅했기 때문에 함께 add가 진행될 것,.)
            ordering.getOrderDetails().add(orderDetail);

        } // end forEach

        // Ordering 객체를 save하면 내부에 있는 detail 리스트도 함께 INSERT 진행이 된다.
        return orderingRepository.save(ordering);
    }

    public List<OrderingListResDto> myOrders(TokenUserInfo userInfo) {
        /*
        OrderingListResDto -> OrderDetailDto(static 내부 클래스)
        {
            id: 주문번호,
            email: 주문한 사람 email,
            orderStatus: 주문상태
            orderDetails: [
                {
                    id: 주문상세 번호,
                    productName: 상품명
                    count: 수량
                },
                {
                    id: 주문상세 번호,
                    productName: 상품명
                    count: 수량
                },
                {
                    id: 주문상세 번호,
                    productName: 상품명
                    count: 수량
                },
                ...
            ]
        }
         */

        // 사용자 정보 가져오기
        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        // 사용자가 주문한 주문 내역 가져오기 <- orderingRepository에 메서드 추가
        List<Ordering> orderingList = orderingRepository.findByUser(user);

        // Ordering 엔터티를 DTO로 변환하자. 주문상세에 대한 변환도 필요함.
        List<OrderingListResDto> dtos = orderingList.stream()
                .map(order -> order.fromEntity())
                .collect(Collectors.toList());

        return dtos;

    }

    public List<OrderingListResDto> orderList() {
        List<Ordering> orderList = orderingRepository.findAll();

        List<OrderingListResDto> dtos = orderList.stream()
                .map(order -> order.fromEntity())
                .collect(Collectors.toList());

        return dtos;
    }

    public Ordering orderCancel(Long id) {
        // 상태를 CANCEL로 변경해 주세요.
        // 클라이언트에게는 변경상태와 주문 id만 넘겨주세요.
        Ordering ordering = orderingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("주문이 없는데...")
        );

        ordering.updateStatus(OrderStatus.CANCELED);  // 더티체킹(save를 하지 않아도 변경을 감지한다.)

        return ordering;
    }
}
