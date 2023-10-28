package com.example.demo.service.impl;

import com.example.demo.entity.OrderDetail;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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
    public List<String> checkRoomIsBooked(Date dayStart, Date dayEnd, List<String> idsRoom) {
        return orderDetailRepository.checkRoomIsBooked(dayStart, dayEnd, idsRoom);
    }

    @Override
    public List<String> checkRoomExist(Date dayStart, Date dayEnd, String id) {
        return orderDetailRepository.checkRoomExist(dayStart, dayEnd, id);
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
    public List<OrderDetail> addAll(List<OrderDetail> orderDetails) {
        List<OrderDetail> list = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            list.add(this.add(orderDetail));
        }
        return list;
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
