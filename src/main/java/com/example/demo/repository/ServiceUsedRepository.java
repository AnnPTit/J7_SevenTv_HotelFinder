package com.example.demo.repository;

import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.ServiceUsed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceUsedRepository extends JpaRepository<ServiceUsed, String> {

    @Query(value = "SELECT * FROM service_used where order_detail_id = ?1 ORDER BY create_at DESC", nativeQuery = true)
    List<ServiceUsed> getAllByOrderDetailId(String id);

}
