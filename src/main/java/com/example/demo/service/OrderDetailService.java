package com.example.demo.service;

import com.example.demo.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface OrderDetailService {

    List<OrderDetail> getList();

    List<OrderDetail> getOrderDetailByOrderId(String id);

    List<String> checkRoomIsBooked(Date dayStart, Date dayEnd, List<String> idsRoom);

    List<String> checkRoomExist(Date dayStart, Date dayEnd, String id);

    Page<OrderDetail> getAll(Pageable pageable);

    OrderDetail getOrderDetailById(String id);

    OrderDetail add(OrderDetail orderDetail);
    List<OrderDetail> addAll(List<OrderDetail> orderDetails);

    void delete(String id);

    void delete(OrderDetail orderDetail);

    Integer getBooking();

}
