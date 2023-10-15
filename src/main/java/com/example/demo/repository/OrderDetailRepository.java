package com.example.demo.repository;

import com.example.demo.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    @Query(value = "SELECT * FROM order_detail where order_id = ?1 and status != 0 ORDER BY update_at DESC", nativeQuery = true)
    List<OrderDetail> getAllByOrderId(String id);

    @Query(value = "SELECT * FROM order_detail where order_id = ?1 ORDER BY update_at DESC", nativeQuery = true)
    OrderDetail getOrderDetailByIdOrder(String id);

    @Query(value = "select od.id from order_detail od where room_id in (:idsRoom)" +
            "and ((check_in_datetime between :dayStart and :dayEnd) "   +
            "OR (check_out_datetime between :dayStart and :dayEnd)) and od.status = 1", nativeQuery = true)
    List<String> checkRoomIsBooked(@Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd, @Param("idsRoom") List<String> idsRoom);

}
