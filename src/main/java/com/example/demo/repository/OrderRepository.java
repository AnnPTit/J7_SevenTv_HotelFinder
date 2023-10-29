package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(value = "select * from `order` ORDER BY update_at DESC", nativeQuery = true)
    Page<Order> findAll(Pageable pageable);

    @Query(value = "select * from `order` ORDER BY update_at DESC", nativeQuery = true)
    Page<Order> findAllByStatus(Pageable pageable);

    @Query(value = "SELECT o.* FROM `order` o\n" +
            "JOIN customer c ON o.customer_id = c.id\n" +
            "WHERE ((:orderCode IS NULL OR o.order_code LIKE CONCAT('%', :orderCode, '%'))\n" +
            "OR (:customerFullname IS NULL OR c.fullname LIKE CONCAT('%', :customerFullname, '%')))\n" +
            "AND (:typeOfOrder IS NULL OR o.type_of_order = :typeOfOrder)\n" +
            "AND (:status IS NULL OR o.status = :status)\n" +
            "AND ((o.create_at >= :startDate OR :startDate IS NULL)\n" +
            "AND (o.create_at <= :endDate OR :endDate IS NULL\n" +
            "  OR (o.create_at >= :startDate AND o.create_at <= :endDate)))" +
            "ORDER BY o.update_at DESC\n", nativeQuery = true)
    Page<Order> loadAndSearch(@Param("orderCode") String orderCode,
                              @Param("typeOfOrder") Boolean typeOfOrder,
                              @Param("status") Integer status,
                              @Param("customerFullname") String customerFullname,
                              @Param("startDate") Date startDate,
                              @Param("endDate") Date endDate,
                              Pageable pageable);

    @Query(value = "SELECT * FROM `order` " +
            "WHERE (:orderCode IS NULL OR order_code LIKE CONCAT('%', :orderCode, '%'))" +
            " AND type_of_order = 1 ORDER BY update_at DESC", nativeQuery = true)
    Page<Order> loadBookRoomOffline(@Param("orderCode") String orderCode, Pageable pageable);

    @Query(value = "SELECT * FROM `order` " +
            "WHERE (:orderCode IS NULL OR order_code LIKE CONCAT('%', :orderCode, '%'))" +
            " AND type_of_order = 0 ORDER BY update_at DESC", nativeQuery = true)
    Page<Order> loadBookRoomOnline(@Param("orderCode") String orderCode, Pageable pageable);

    @Query(value = "SELECT COUNT(od.id) FROM Order od WHERE od.status = 0")
    Long countOrderCancel();

    @Query(value = "SELECT COUNT(od.id) FROM Order od WHERE od.status = 1")
    Long countOrderWait();

    @Query(value = "SELECT COUNT(od.id) FROM Order od WHERE od.status = 2")
    Long countOrderConfirm();

    @Query(value = "SELECT COUNT(od.id) FROM Order od WHERE od.status = 3")
    Long countOrderAccept();

    @Query(value = "SELECT * FROM `order` WHERE id = :id", nativeQuery = true)
    Order getById(String id);

    @Query(value = "select * from `order` o where order_code =:code", nativeQuery = true)
    Order getByCode(@Param("code") String code);

}
