package com.example.demo.repository;

import com.example.demo.dto.RevenueDTO;
import com.example.demo.entity.Order;
import com.example.demo.repository.custom.OrderRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, OrderRepositoryCustom {

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

    @Query(value = "SELECT * FROM `order` ORDER BY create_at DESC", nativeQuery = true)
    List<Order> findAll();

    @Query(value = "SELECT o.* FROM `order` o " +
            " WHERE o.type_of_order = 0 AND o.status = 1 ORDER BY o.update_at DESC", nativeQuery = true)
    List<Order> loadNotify();

    @Query(value = "SELECT o.* FROM `order` o " +
            "JOIN customer c ON o.customer_id = c.id\n" +
            "WHERE ((:orderCode IS NULL OR o.order_code LIKE CONCAT('%', :orderCode, '%'))" +
            "OR (:customerFullname IS NULL OR c.fullname LIKE CONCAT('%', :customerFullname, '%'))" +
            "OR (:customerPhone IS NULL OR c.phone_number LIKE CONCAT('%', :customerPhone, '%'))" +
            "OR (:customerEmail IS NULL OR c.email LIKE CONCAT('%', :customerEmail, '%')))\n" +
            " AND o.type_of_order = 0 AND (:status IS NULL OR o.status = :status) ORDER BY o.update_at DESC", nativeQuery = true)
    Page<Order> loadBookRoomOnline(@Param("orderCode") String orderCode,
                                   @Param("customerFullname") String customerFullname,
                                   @Param("customerPhone") String customerPhone,
                                   @Param("customerEmail") String customerEmail,
                                   @Param("status") Integer status, Pageable pageable);

    @Query(value = "SELECT * FROM `order` o " +
            "WHERE (:orderCode IS NULL OR o.order_code LIKE CONCAT('%', :orderCode, '%'))" +
            " AND o.type_of_order = 1 AND (:status IS NULL OR o.status = :status) ORDER BY o.update_at DESC", nativeQuery = true)
    Page<Order> loadBookRoomOffline(@Param("orderCode") String orderCode,
                                    @Param("status") Integer status, Pageable pageable);

    @Query(value = "SELECT COUNT(od.id) FROM Order od WHERE od.status IN (0, 6, 8, 9)")
    Long countOrderCancel();

    @Query(value = "SELECT COUNT(od.id) FROM Order od WHERE od.status = 1")
    Long countOrderWait();

    @Query(value = "SELECT COUNT(od.id) FROM Order od WHERE od.status = 2")
    Long countOrderConfirm();

    @Query(value = "SELECT COUNT(od.id) FROM Order od WHERE od.status = 3")
    Long countOrderAccept();

    @Query(value = "SELECT COUNT(od.id) FROM Order od WHERE od.status = 4")
    Long countOrderConfirmInfo();

    @Query(value = "SELECT COUNT(od.id) FROM Order od WHERE od.status = 5")
    Long countOrderPaymentDeposit();

    @Query(value = "SELECT * FROM `order` WHERE id = :id", nativeQuery = true)
    Order getById(String id);

    @Query(value = "select * from `order` o where order_code =:code", nativeQuery = true)
    Order getByCode(@Param("code") String code);

    @Query(value = "SELECT SUM(total_money) FROM `order` " +
            "WHERE DATE(update_at) = DATE(CURRENT_DATE()) AND `status` = 3", nativeQuery = true)
    BigDecimal getRevenueMonth();

    @Query(value = "SELECT SUM(total_money) FROM `order` " +
            "WHERE YEAR(update_at) = YEAR(CURRENT_DATE()) AND `status` = 3", nativeQuery = true)
    BigDecimal getRevenueYear();

    @Modifying
    @Transactional
    @Query(value = "update `order` o set o.status = :stt where o.id =:id", nativeQuery = true)
    void updateStatus(@Param("id") String id, @Param("stt") Integer stt);

    @Modifying
    @Transactional
    @Query(value = "update `order` o set o.status = :stt, o.refuse_reason =:refuseReason where o.id =:id", nativeQuery = true)
    void updateStatus(@Param("id") String id, @Param("stt") Integer stt, @Param("refuseReason") String refuseReason);


    @Query(value = "select o.*  from `order` o inner join order_detail od on od.order_id  = o.id \n" +
            "where od.room_id =:idRoom and o.status  in (1,2,4,5)", nativeQuery = true)
    List<Order> getRoomInOrder(@Param("idRoom") String idRoom);
}
