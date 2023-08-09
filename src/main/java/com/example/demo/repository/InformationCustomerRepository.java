package com.example.demo.repository;

import com.example.demo.entity.InformationCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InformationCustomerRepository extends JpaRepository<InformationCustomer, String> {

    @Query(value = "SELECT * FROM information_customer WHERE order_detail_id = ?1", nativeQuery = true)
    List<InformationCustomer> findAllByOrderDetailId(String id);

}
