package com.playdata.orderservice.ordering.controller;

import com.playdata.orderservice.common.auth.TokenUserInfo;
import com.playdata.orderservice.common.dto.CommonResDto;
import com.playdata.orderservice.ordering.dto.OrderingSaveReqDto;
import com.playdata.orderservice.ordering.entity.Ordering;
import com.playdata.orderservice.ordering.service.OrderingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderingController {

    private final OrderingService orderingService; // 의존성 주입

    @PostMapping("/create")
    public ResponseEntity<?> orderingCreate(
            // 전역 인증 정보를 담아놓는 SecurityContextHolder에서 메서드 호출시에
            // 사용자 인증 정보를 전달해 달라는 어노테이션 -> @AuthenticationPrincipal
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody List<OrderingSaveReqDto> dtoList){

        Ordering ordering = orderingService.createOrdering(dtoList, userInfo);

        CommonResDto resDto =
                new CommonResDto(HttpStatus.CREATED, "정상 주문 완료", ordering.getId());

        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }
}
