package com.playdata.orderservice.user.service;

import com.playdata.orderservice.common.auth.TokenUserInfo;
import com.playdata.orderservice.user.dto.UserLoginReqDto;
import com.playdata.orderservice.user.dto.UserResDto;
import com.playdata.orderservice.user.dto.UserSaveReqDto;
import com.playdata.orderservice.user.entity.User;
import com.playdata.orderservice.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    // 서비스 -> 엔터티 -> 레포지토리 -> DB
    private final UserRepository userRepository; // @RequiredArgsConstructor에 의해 Repository 주입
    private final PasswordEncoder encoder;
    private final PageableHandlerMethodArgumentResolver pageableResolver;

    public User userCreate(@Valid UserSaveReqDto dto) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        User save = userRepository.save(dto.toEntity(encoder));
        log.info(save.toString());
        return save;

    }

    public User login(UserLoginReqDto dto) {
        // email로 user 조회하기
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() ->
            new EntityNotFoundException("User Not Found")
        );

        // 비밀번호 확인하기(암호와 되어있으니, encoder에게 부탁)
        if(!encoder.matches(dto.getPassword(), user.getPassword())){
            throw new  IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public UserResDto myinfo(){
        TokenUserInfo userInfo
                // 필터에서 세팅한 토큰 정보를 불러오는 메서드(SecurityContextHolder)
                = (TokenUserInfo) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("User Not Found")
        );

        return user.fromEntity();

    }

    public List<UserResDto> userList(Pageable pageable) {
        // UserResDto가 여러개 리턴되어야 함.
        // 페이징 처리해 주세요. 1페이지 요청, 한화면에 보여줄 회원수: 6명

//        Pageable pageable = PageRequest.of(0, 6);  --> 필요없어짐. Controller에서 넘겨줌
        Page<User> users = userRepository.findAll(pageable);

        // 실질적 데이터(userList)
        List<User> content = users.getContent();
        List<UserResDto> dtoList = content.stream().map(user -> user.fromEntity())
                .collect(Collectors.toList());

        // 총 페이지 수
        int totalPages = users.getTotalPages();

        // 총 데이터 수
        long total = users.getTotalElements();

        return dtoList;

    }
}
