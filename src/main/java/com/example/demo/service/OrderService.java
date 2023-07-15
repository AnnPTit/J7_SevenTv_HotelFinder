package com.example.demo.service;

import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<Order> getAll(Pageable pageable);

    Order getOrderById(String id);

    Order add(Order order);

    void delete(String id);

}
