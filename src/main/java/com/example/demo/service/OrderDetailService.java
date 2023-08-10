package com.example.demo.service;

import com.example.demo.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderDetailService {

    List<OrderDetail> getList();

    List<OrderDetail> getOrderDetailByOrderId(String id);

    OrderDetail getOrderDetailByIdOrder(String id);

    Page<OrderDetail> getAll(Pageable pageable);

    OrderDetail getOrderDetailById(String id);

    OrderDetail add(OrderDetail orderDetail);

    void delete(String id);

}
