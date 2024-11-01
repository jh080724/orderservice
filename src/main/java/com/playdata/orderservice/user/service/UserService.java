package com.playdata.orderservice.user.service;

import com.playdata.orderservice.user.dto.UserSaveReqDto;
import com.playdata.orderservice.user.entity.User;
import com.playdata.orderservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    // 서비스 -> 엔터티 -> 레포지토리 -> DB
    private final UserRepository userRepository; // @RequiredArgsConstructor에 의해 Repository 주입
    private final PasswordEncoder encoder;

    public User userCreate(@Valid UserSaveReqDto dto) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }
        return userRepository.save(dto.toEntity(encoder));
    }
}
