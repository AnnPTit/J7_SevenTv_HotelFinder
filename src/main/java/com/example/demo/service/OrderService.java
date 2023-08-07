package com.example.demo.service;

import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    List<Order> getList();

    Page<Order> getAll(Pageable pageable);

    Page<Order> getAllByStatus(Pageable pageable);

    Order getOrderById(String id);

    Order add(Order order);

    void delete(String id);

}
