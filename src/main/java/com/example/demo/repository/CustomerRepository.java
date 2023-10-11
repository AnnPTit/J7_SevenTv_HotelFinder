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

    @Query(value = "SELECT * FROM customer WHERE status = 1 ORDER BY updateAt DESC ", nativeQuery = true)
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

    @Query(value = "SELECT * FROM customer where customer_code = 'KH00'", nativeQuery = true)
    Customer getCustomerByCode();

    @Query(value = "SELECT * FROM customer where customer_code = ?1", nativeQuery = true)
    Customer findByCustomerCode(String customerCode);

    @Query(value = "SELECT * FROM customer where id = ?1 and status = 1", nativeQuery = true)
    Customer getCustomerById(String id);

    @Query(value = "select * from  customer where email =?1 and status = 1", nativeQuery = true)
    Optional<Customer> findCustomerByEmail(String email);

    @Query(value = "SELECT c.*\n" +
            "FROM customer c\n" +
            "INNER JOIN information_customer ic ON c.citizen_id = ic.citizen_id\n" +
            "INNER JOIN order_detail od ON ic.order_detail_id = od.id\n" +
            "INNER JOIN `order` o ON od.order_id = o.id\n" +
            "WHERE o.id = ?1 AND ic.citizen_id IS NOT NULL;", nativeQuery = true)
    List<Customer> getAllCustomer(String id);

    @Query(value = "SELECT c.*\n" +
            "FROM customer c\n" +
            "INNER JOIN information_customer ic ON c.citizen_id = ic.citizen_id\n" +
            "INNER JOIN order_detail od ON ic.order_detail_id = od.id\n" +
            "INNER JOIN `order` o ON od.order_id = o.id\n" +
            "WHERE od.id = ?1 AND ic.citizen_id IS NOT NULL;", nativeQuery = true)
    List<Customer> getAllCustomerByOrderDetailId(String id);

}
