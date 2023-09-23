package com.example.demo.service.impl;

import com.example.demo.entity.OrderDetail;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetail> getList() {
        return orderDetailRepository.findAll();
    }

    @Override
    public List<OrderDetail> getOrderDetailByOrderId(String id) {
        return orderDetailRepository.getAllByOrderId(id);
    }

    @Override
    public OrderDetail getOrderDetailByIdOrder(String id) {
        return orderDetailRepository.getOrderDetailByIdOrder(id);
    }

    @Override
    public Page<OrderDetail> getAll(Pageable pageable) {
        return orderDetailRepository.findAll(pageable);
    }

    @Override
    public OrderDetail getOrderDetailById(String id) {
        return orderDetailRepository.findById(id).orElse(null);
    }

    @Override
    public OrderDetail add(OrderDetail orderDetail) {
        try {
            return orderDetailRepository.save(orderDetail);
        } catch (Exception e) {
            System.out.println("Add error!");
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            orderDetailRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete error!");
        }
    }

    @Override
    public void delete(OrderDetail orderDetail) {
        try {
            orderDetailRepository.delete(orderDetail);
        } catch (Exception e) {
            System.out.println("Delete error!");
        }
    }
}
