package com.example.demo.repository;

import com.example.demo.entity.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {

    @Query(value = "SELECT * FROM payment_method where order_id = ?1 ORDER BY update_at DESC", nativeQuery = true)
    List<PaymentMethod> getAllByOrderId(String id);

    @Query(value = "SELECT pm FROM PaymentMethod pm JOIN pm.order o JOIN o.customer c" +
            " WHERE ((:orderCode IS NULL OR o.orderCode LIKE CONCAT('%', :orderCode, '%'))" +
            " OR (:customerFullname IS NULL OR c.fullname LIKE CONCAT('%', :customerFullname, '%')))" +
            " AND (:method IS NULL OR pm.method = :method)" +
            "AND ((pm.createAt >= :startDate OR :startDate IS NULL)\n" +
            "AND (pm.createAt <= :endDate OR :endDate IS NULL\n" +
            "  OR (pm.createAt >= :startDate AND pm.createAt <= :endDate)))" +
            " ORDER BY pm.createAt DESC")
    Page<PaymentMethod> loadAndSearch(@Param("orderCode") String orderCode,
                                      @Param("method") Boolean method,
                                      @Param("customerFullname") String customerFullname,
                                      @Param("startDate") Date startDate,
                                      @Param("endDate") Date endDate,
                                      Pageable pageable);

}
