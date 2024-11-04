package com.playdata.orderservice.ordering.repository;

import com.playdata.orderservice.ordering.entity.Ordering;
import com.playdata.orderservice.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderingRepository extends JpaRepository<Ordering, Long> {

//    @Query("SELECT o FROM Ordering o WHERE o.user = ?1")
    List<Ordering> findByUser(User user); // Join 컬럼이 user_id로 조회해줌.

}
