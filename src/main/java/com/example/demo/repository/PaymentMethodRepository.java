package com.example.demo.repository;

import com.example.demo.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {

    @Query(value = "SELECT * FROM payment_method where order_id = ?1 ORDER BY create_at DESC", nativeQuery = true)
    List<PaymentMethod> getAllByOrderId(String id);

}
