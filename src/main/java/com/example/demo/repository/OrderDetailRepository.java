package com.example.demo.repository;

import com.example.demo.entity.OrderDetail;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    @Query(value = "SELECT * FROM order_detail where order_id = ?1 and status != 0 ORDER BY update_at DESC", nativeQuery = true)
    List<OrderDetail> getAllByOrderId(String id);

    @Query(value = "select\n" +
            "                      *\n" +
            "                       from\n" +
            "                       order_detail od\n" +
            "                       inner join `order` o on\n" +
            "                       o.id = od.order_id\n" +
            "                       where\n" +
            "                       od.room_id in :idsRoom\n" +
            "                       and (\n" +
            "                       ((DATE(od.check_in_datetime)  between :dayStart and :dayEnd) and o.status  not in(0, 3, 6, 7,8))\n" +
            "                       and \n" +
            "                       ((DATE(od.check_out_datetime)  between :dayStart and :dayEnd) and o.status not in(0, 3, 6, 7,8))\n" +
            "                      )", nativeQuery = true)
    List<String> checkRoomIsBooked(@Param("dayStart") String dayStart, @Param("dayEnd") String dayEnd, @Param("idsRoom") List<String> idsRoom);

    @Query(value = "select od.id from order_detail od where room_id = :idRoom " +
            "AND ((check_in_datetime between :dayStart and :dayEnd) " +
            "OR (check_out_datetime between :dayStart and :dayEnd)) and od.status = 1", nativeQuery = true)
    List<String> checkRoomExist(@Param("dayStart") LocalDateTime dayStart, @Param("dayEnd") LocalDateTime dayEnd, @Param("idRoom") String idRoom);

    @Query(value = "SELECT COUNT(`status`) FROM order_detail " +
            "WHERE DATE(update_at) = DATE(current_date()) AND `status` IN (1, 2, 3)", nativeQuery = true)
    Integer getBooking();

    @Query(value = "select * from order_detail od where od.room_id in :roomId ", nativeQuery = true)
    List<OrderDetail> getOrderByRoomIds(@Param("roomId") List<String> roomId);

    @Query(value = "select od.* from order_detail od \n" +
            "inner join `order` o on o.id = od.order_id  and o.status not in (0,3,6,7,8)\n" +
            "where od.room_id = :roomId  ", nativeQuery = true)
    List<OrderDetail> getOrderByRoomId(@Param("roomId") String roomId);

    @Modifying
    @Transactional
    @Query(value = " update order_detail od set od.status =:stt where od.order_id =:id", nativeQuery = true)
    void updateStatusByOrderId(@Param("id") String id, @Param("stt") Integer stt);
}
