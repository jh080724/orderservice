package com.playdata.orderservice.user.controller;

import com.playdata.orderservice.common.auth.JwtTokenProvider;
import com.playdata.orderservice.common.dto.CommonResDto;
import com.playdata.orderservice.user.dto.UserLoginReqDto;
import com.playdata.orderservice.user.dto.UserResDto;
import com.playdata.orderservice.user.dto.UserSaveReqDto;
import com.playdata.orderservice.user.entity.User;
import com.playdata.orderservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    // 컨트롤러는 서비스에 의존한다., Controller -> Service -> Repository -> DB
    private final UserService userService; // 서비스 주입
    private final JwtTokenProvider jwtTokenProvider; // 토큰 생성 주입

    @PostMapping("/create")
    public ResponseEntity<?> userCreate(@Valid @RequestBody UserSaveReqDto dto) {

        log.info("userCreate dto: {}", dto);
        User user = userService.userCreate(dto);

        CommonResDto resDto
                = new CommonResDto(HttpStatus.CREATED, "member create 성공", user.getId());

        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody UserLoginReqDto dto) {

        log.info("doLogin 파라미터 UserLoginReqDto: {}", dto);

        User user = userService.login(dto);


        // 회원 정보가 일치한다면, JWT를 생성해서 클라이언트에게 발급해주어야 한다.
        // 로그인 유지를 위해 발급
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().toString());
        log.info("token: {}", token);

        // 생성된 토큰 외에 추가로 전달할 정보가 있다면 Map을 사용하는 것이 좋다.
        Map<String, Object> logInfo = new HashMap<>();
        logInfo.put("token", token);
        logInfo.put("id", user.getId());

        // 리턴 Entity -> ResponseEntity<>(resDto)에 포함됨.
        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "로그인 성공!", logInfo);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 회원정보 조회(관리자) -> ADMIN만 회원목록 전체를 조회할 수 있다.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    // 컨트롤러 파라미터로 Pageable 선언하면, 페이징 파리미터 처리를 쉽게 진행할 수 있음.
    // list?number=1&size-10&sort=name,desc 와 같이 사용하면 됨.
    // 요청시 쿼리 스트링이 전달되지 않으면, 기본값(0, 20, unsorted)으로 처리됨.
    public ResponseEntity<?> userList(Pageable pageable) {
        log.info("/user/list: GET!!!");
        log.info("pageable: {}", pageable);

        List<UserResDto> userResDtos = userService.userList(pageable);

        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "userList 조회 성공", userResDtos);

        return ResponseEntity.ok().body(resDto);
    }


    // 회원정보 조회(마이페이지)
    @GetMapping("/myinfo")
    public ResponseEntity<?> myInfo() {
        UserResDto dto = userService.myinfo();
        CommonResDto resDto = new CommonResDto(HttpStatus.OK, "myInfo 조회성공!", dto);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

}
