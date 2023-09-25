package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(value = "select * from `order` ORDER BY create_at DESC", nativeQuery = true)
    Page<Order> findAll(Pageable pageable);

    @Query(value = "select * from `order` ORDER BY create_at DESC", nativeQuery = true)
    Page<Order> findAllByStatus(Pageable pageable);

    @Query(value = "SELECT * FROM `order` " +
            "WHERE (:orderCode IS NULL OR order_code LIKE CONCAT('%', :orderCode, '%')) " +
            "AND (:typeOfOrder IS NULL OR type_of_order = :typeOfOrder)\n" +
            "AND (:status IS NULL OR status = :status)\n" +
            "ORDER BY create_at DESC", nativeQuery = true)
    Page<Order> loadAndSearch(@Param("orderCode") String orderCode,
                              @Param("typeOfOrder") Boolean typeOfOrder,
                              @Param("status") Integer status,
                              Pageable pageable);   

    @Query(value = "SELECT * FROM `order` " +
            "WHERE (:orderCode IS NULL OR order_code LIKE CONCAT('%', :orderCode, '%'))" +
            " AND type_of_order = 1 ORDER BY create_at DESC", nativeQuery = true)
    Page<Order> loadBookRoomOffline(@Param("orderCode") String orderCode, Pageable pageable);


}
