package com.playdata.orderservice.product.controller;

import com.playdata.orderservice.common.dto.CommonResDto;
import com.playdata.orderservice.product.dto.ProductResDto;
import com.playdata.orderservice.product.dto.ProductSaveReqDto;
import com.playdata.orderservice.product.entity.Product;
import com.playdata.orderservice.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;    // 의존성 주입

    @PreAuthorize("hasRole('ADMIN')")  // ADMIN 회원만 보낼 수 있게 처리.
    @PostMapping("/create")
    // 요청과 함께 이미지가 전달이 될 것이다.
    // 해당 이미지를 처리하는 방식이 두 가지로 나뉜다.
    // 1. JS의 FormDate 객체를 통해서 모든 데이터를 전달. <<(인코딩 타입: multipart/form-data 형식, 태그아님)
    // 2. JSON 형태로 전달(이미지를 Base64 인코딩을 통해 문자열로 변환해서 전달)
    // ModelAttribute를 사용해서 FormData 객체를 우리가 평소에 사용하는 from 태그 방식으로 받겠다.
    // Model 기능을 쓰겠다는 것이 아님.(React 단에는 model을 전달할 수 없음)
    public ResponseEntity<?> createProduct(@ModelAttribute ProductSaveReqDto dto) {
        log.info("/product/create: POST");

        Product product = productService.productCreate(dto);

        CommonResDto resDto
                = new CommonResDto(HttpStatus.CREATED, "product 등록성공", product.getId());

        return new ResponseEntity<>(resDto, HttpStatus.CREATED);

    }

    @GetMapping("/list")
    // 페이징이 필요합니다. 리턴은 ProductResDto 형태로 리턴됩니다.
    // ProductResDto에는 id, name, category, price, stockQuantity, imagePath가 있음.
    public ResponseEntity<?> listProduct(Pageable pageable) {
        log.info("/product/list: GET, pageable={}", pageable);

        List<ProductResDto> dtoList = productService.productList(pageable);

        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK, "상품리스트 정상 조회 완료", dtoList);

        return new ResponseEntity<>(resDto, HttpStatus.OK);

    }
}
