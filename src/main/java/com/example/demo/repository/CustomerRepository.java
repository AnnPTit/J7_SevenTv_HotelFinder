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

    @Query(value = "SELECT * FROM customer WHERE status = 1 ORDER BY update_at DESC", nativeQuery = true)
    Page<Customer> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM customer WHERE status = 1 " +
            "AND ((:citizenId is null or citizen_id LIKE CONCAT('%', :citizenId ,'%'))" +
            "OR (:fullname is null or fullname LIKE CONCAT('%', :fullname ,'%'))" +
            "OR (:phoneNumber is null or phone_number LIKE CONCAT('%', :phoneNumber ,'%')))" +
            "ORDER BY update_at DESC", nativeQuery = true)
    List<Customer> getAllByStatus(@Param("citizenId") String citizenId,
                                  @Param("fullname") String fullname,
                                  @Param("phoneNumber") String phoneNumber);

    @Query(value = "select * from customer", nativeQuery = true)
    List<Customer> getAllCustomer();

    Optional<Customer> findByEmail(String email);

    @Query(value = "select * from customer c where c.citizen_id =:citizenId and status =1 ",nativeQuery = true)
    List<Customer> findByCitizenId(@Param("citizenId") String citizenId);

    @Query(value = "SELECT * FROM customer " +
            " WHERE ((:customerCode IS NULL OR customer_code = :customerCode) " +
            " OR (:fullname IS NULL OR fullname LIKE CONCAT('%', :fullname ,'%')) " +
            " OR (:phoneNumber IS NULL OR phone_number = :phoneNumber)) AND status = 1 ORDER BY update_at DESC \n", nativeQuery = true)
    Page<Customer> loadAndSearch(@Param("customerCode") String customerCode,
                                 @Param("fullname") String fullname,
                                 @Param("phoneNumber") String phoneNumber,
                                 Pageable pageable);

    @Query(value = "SELECT * FROM customer where customer_code = 'KH00'", nativeQuery = true)
    Customer getCustomerByCode();

    @Query(value = "SELECT * FROM customer where customer_code = ?1 and status =1 ", nativeQuery = true)
    Customer findByCustomerCode(String customerCode);

    @Query(value = "SELECT * FROM customer where username = ?1 and status=1", nativeQuery = true)
    Customer findByUsername(String userName);

    @Query(value = "SELECT * FROM customer where id = ?1 and status = 1", nativeQuery = true)
    Customer getCustomerById(String id);

    @Query(value = "select * from  customer where email =?1 and status = 1", nativeQuery = true)
    Optional<Customer> findCustomerByEmail(String email);

    @Query(value = "SELECT c.*\n" +
            "FROM customer c\n" +
            "INNER JOIN information_customer ic ON c.citizen_id = ic.citizen_id\n" +
            "INNER JOIN order_detail od ON ic.order_detail_id = od.id\n" +
            "INNER JOIN `order` o ON od.order_id = o.id\n" +
            "WHERE o.id = ?1 AND ic.citizen_id IS NOT NULL ORDER BY c.update_at DESC", nativeQuery = true)
    List<Customer> getAllCustomer(String id);

    @Query(value = "SELECT c.*\n" +
            "FROM customer c\n" +
            "INNER JOIN information_customer ic ON c.citizen_id = ic.citizen_id\n" +
            "INNER JOIN order_detail od ON ic.order_detail_id = od.id\n" +
            "INNER JOIN `order` o ON od.order_id = o.id\n" +
            "WHERE od.id = ?1 AND ic.citizen_id IS NOT NULL ORDER BY c.update_at DESC", nativeQuery = true)
    List<Customer> getAllCustomerByOrderDetailId(String id);

    @Query(value = "SELECT DISTINCT c.*\n" +
            "FROM customer c\n" +
            "INNER JOIN information_customer ic ON c.citizen_id = ic.citizen_id\n" +
            "INNER JOIN order_detail od ON ic.order_detail_id = od.id\n" +
            "INNER JOIN `order` o ON od.order_id = o.id\n" +
            "WHERE o.id = ?1 AND ic.citizen_id IS NOT NULL\n" +
            "AND c.id NOT IN (\n" +
            "    SELECT DISTINCT c1.id\n" +
            "    FROM customer c1\n" +
            "    INNER JOIN information_customer ic1 ON c1.citizen_id = ic1.citizen_id\n" +
            "    INNER JOIN order_detail od1 ON ic1.order_detail_id = od1.id\n" +
            "    INNER JOIN `order` o ON o.id = od1.order_id\n" +
            "    WHERE od1.id = ?2 AND ic1.citizen_id IS NOT NULL\n" +
            ")\n" +
            "ORDER BY c.update_at DESC;", nativeQuery = true)
    List<Customer> getCustomerDifferentOrder(String orderId, String orderDetailId);

    @Query(value = "SELECT COUNT(cus.id) FROM Customer cus WHERE cus.status = 1")
    Long countCustomerByStatus();

    boolean existsByEmail(String email);

}
