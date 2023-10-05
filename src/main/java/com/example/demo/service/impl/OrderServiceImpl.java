package com.example.demo.service.impl;

import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Order> getList() {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> getAllByStatus(Pageable pageable) {
        return orderRepository.findAllByStatus(pageable);
    }

    @Override
    public Page<Order> loadAndSearch(String orderCode, Boolean typeOfOrder, Integer status, String customerFullname, Date startDate, Date endDate, Pageable pageable) {
        return orderRepository.loadAndSearch((orderCode != null && !orderCode.isEmpty()) ? orderCode : null,
                (typeOfOrder != null && !typeOfOrder.toString().isEmpty()) ? typeOfOrder : null,
                (status != null && !status.toString().isEmpty()) ? status : null,
                (customerFullname != null && !customerFullname.isEmpty()) ? customerFullname : null,
                startDate,
                endDate,
                pageable);
    }

    @Override
    public Page<Order> loadBookRoomOffline(String orderCode, Pageable pageable) {
        return orderRepository.loadBookRoomOffline((orderCode != null && !orderCode.isEmpty()) ? orderCode : null, pageable);
    }

    @Override
    public Page<Order> loadBookRoomOnline(String orderCode, Pageable pageable) {
        return orderRepository.loadBookRoomOnline(orderCode, pageable);
    }

    @Override
    public Order getOrderById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order add(Order order) {
        try {
            return orderRepository.save(order);
        } catch (Exception e) {
            System.out.println("Add error!");
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete error!");
        }
    }
}
