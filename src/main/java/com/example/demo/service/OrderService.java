package com.example.demo.service;

import com.example.demo.entity.Order;

import java.util.List;

public interface OrderService {

    List<Order> getAll();

    Order getOrderById(String id);

    Order add(Order order);

    void delete(String id);

}
