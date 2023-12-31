package com.example.demo.service;

import com.example.demo.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OrderDetailService {

    List<OrderDetail> getList();

    List<OrderDetail> getOrderDetailByOrderId(String id);

    List<String> checkRoomIsBooked(String dayStart, String dayEnd, List<String> idsRoom);

    List<String> checkRoomExist(String dayStart, String dayEnd, String id);

    Page<OrderDetail> getAll(Pageable pageable);

    OrderDetail getOrderDetailById(String id);

    OrderDetail add(OrderDetail orderDetail);

    List<OrderDetail> addAll(List<OrderDetail> orderDetails);

    void delete(String id);

    void delete(OrderDetail orderDetail);

    Integer getBooking();

    List<String> getOrderByRoomIds(List<String> roomId);
     List<String> getStartDate(List<String> roomId);
     List<String> getEndDate(List<String> roomId);

}
