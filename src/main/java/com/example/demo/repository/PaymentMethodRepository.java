package com.example.demo.repository;

import com.example.demo.entity.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {

    @Query(value = "SELECT * FROM payment_method where order_id = ?1 ORDER BY create_at DESC", nativeQuery = true)
    List<PaymentMethod> getAllByOrderId(String id);

    @Query(value = "SELECT pm FROM PaymentMethod pm JOIN pm.order o" +
            " WHERE (:orderCode IS NULL OR o.orderCode LIKE CONCAT('%', :orderCode, '%')) ORDER BY o.createAt DESC")
    Page<PaymentMethod> loadAndSearch(@Param("orderCode") String orderCode, Pageable pageable);

}
