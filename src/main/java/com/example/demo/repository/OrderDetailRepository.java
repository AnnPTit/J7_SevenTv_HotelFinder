package com.example.demo.repository;

import com.example.demo.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    @Query(value = "SELECT * FROM order_detail where order_id = ?1 ORDER BY create_at ASC", nativeQuery = true)
    List<OrderDetail> getAllByOrderId(String id);

    @Query(value = "SELECT * FROM order_detail where order_id = ?1 ORDER BY create_at ASC", nativeQuery = true)
    OrderDetail getOrderDetailByIdOrder(String id);

}
