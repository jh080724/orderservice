package com.playdata.orderservice.product.service;

import com.playdata.orderservice.product.dto.ProductSaveReqDto;
import com.playdata.orderservice.product.entity.Product;
import com.playdata.orderservice.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public Product productCreate(ProductSaveReqDto dto) {
        MultipartFile productImage = dto.getProductImage();

        String uniqueFileName
                = UUID.randomUUID() + "_" + productImage.getOriginalFilename();

        File file = new File("c:/develop/upload/" + uniqueFileName);

        try {
            productImage.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패");
        }

        Product product = dto.toEntity();
        product.updateImagePath(uniqueFileName);

        return productRepository.save(product);
    }
}
