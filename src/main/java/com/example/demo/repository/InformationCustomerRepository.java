package com.example.demo.repository;

import com.example.demo.entity.InformationCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InformationCustomerRepository extends JpaRepository<InformationCustomer, String> {

    @Query(value = "SELECT * FROM information_customer WHERE order_detail_id = ?1 ORDER BY update_at DESC", nativeQuery = true)
    List<InformationCustomer> findAllByOrderDetailId(String id);

    @Query(value = "SELECT ic.*\n" +
            "FROM `information_customer` ic\n" +
            "JOIN `order_detail` od ON ic.order_detail_id = od.id\n" +
            "JOIN `order` o ON od.order_id = o.id\n" +
            "WHERE o.id = ?1 ORDER BY update_at DESC", nativeQuery = true)
    List<InformationCustomer> findAllByOrderId(String id);

}
