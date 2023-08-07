package com.example.demo.repository;

import com.example.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query(value = "SELECT * FROM customer where id = '66e9d662-33a3-11ee-8f16-489ebddaf682'", nativeQuery = true)
    Customer getCustomerById();

}
