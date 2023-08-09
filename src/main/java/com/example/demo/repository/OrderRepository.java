package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(value = "select * from `order` ORDER BY create_at DESC", nativeQuery = true)
    Page<Order> findAll(Pageable pageable);

    @Query(value = "select * from `order` ORDER BY create_at DESC", nativeQuery = true)
    Page<Order> findAllByStatus(Pageable pageable);

}
