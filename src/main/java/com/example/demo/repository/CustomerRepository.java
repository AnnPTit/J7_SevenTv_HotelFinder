package com.example.demo.repository;

import com.example.demo.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query(value = "select * from customer where status =1", nativeQuery = true)
    Page<Customer> findAll(Pageable pageable);

    @Query(value = "select * from customer where status =1", nativeQuery = true)
    List<Customer> getAll();

    Optional<Customer> findByEmail(String email);

    Customer findByCitizenId(String citizenId);

    @Query(value = "SELECT * FROM customer " +
            " WHERE status = 1 AND ((:customerCode IS NULL OR customer_code = :customerCode) " +
            " OR (:fullname IS NULL OR fullname LIKE CONCAT('%', :fullname ,'%')) " +
            " OR (:phoneNumber IS NULL OR phone_number = :phoneNumber))\n", nativeQuery = true)
    Page<Customer> loadAndSearch(@Param("customerCode") String customerCode,
                                 @Param("fullname") String fullname,
                                 @Param("phoneNumber") String phoneNumber,
                                 Pageable pageable);

    Customer findByCustomerCode(String code);

    @Query(value = "SELECT * FROM customer where customer_code = 'KH00'", nativeQuery = true)
    Customer getCustomerByCode();


}
