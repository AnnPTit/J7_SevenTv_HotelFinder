package com.example.demo.repository;

import com.example.demo.entity.OrderTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTimelineRepository extends JpaRepository<OrderTimeline, String> {

    @Query(value = "SELECT * FROM order_timeline where order_id = ?1 ORDER BY create_at ASC",nativeQuery = true)
    List<OrderTimeline> getOrderTimelineByOrderId(String id);

}
