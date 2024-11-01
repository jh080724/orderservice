package com.playdata.orderservice.user.repository;

import com.playdata.orderservice.user.entity.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // User 꺼내고, Null Check, 쿼리

}
