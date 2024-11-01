package com.playdata.orderservice.user.controller;

import com.playdata.orderservice.common.dto.CommonResDto;
import com.playdata.orderservice.user.dto.UserSaveReqDto;
import com.playdata.orderservice.user.entity.User;
import com.playdata.orderservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    // 컨트롤러는 서비스에 의존한다., Controller -> Service -> Repository -> DB
    private final UserService userService; // 서비스 주입

    @PostMapping("/create")
    public ResponseEntity<?> userCreate(@Valid @RequestBody UserSaveReqDto dto){

        User user = userService.userCreate(dto);

        CommonResDto resDto
                = new CommonResDto(HttpStatus.CREATED, "member create 성공", user.getId());

        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }
}
