package com.example.demo.repository;

import com.example.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    Customer findByCustomerCode(String code);

    @Query(value = "SELECT * FROM customer where customer_code = 'KH00'", nativeQuery = true)
    Customer getCustomerByCode();

    @Query(value = "SELECT * FROM customer where id = ?1 and status = 1", nativeQuery = true)
    Customer getCustomerById(String id);

    @Query(value = "select * from  customer where email =?1 and status = 1", nativeQuery = true)
    Optional<Customer> findCustomerByEmail(String email);


}
