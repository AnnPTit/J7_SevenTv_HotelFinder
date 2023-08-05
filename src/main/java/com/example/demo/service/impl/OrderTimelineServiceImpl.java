package com.example.demo.service.impl;

import com.example.demo.entity.OrderTimeline;
import com.example.demo.repository.OrderTimelineRepository;
import com.example.demo.service.OrderTimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderTimelineServiceImpl implements OrderTimelineService {

    @Autowired
    private OrderTimelineRepository orderTimelineRepository;

    @Override
    public List<OrderTimeline> getAll() {
        return orderTimelineRepository.findAll();
    }

    @Override
    public List<OrderTimeline> getOrderTimelineByOrderId(String id) {
        return orderTimelineRepository.getOrderTimelineByOrderId(id);
    }

    @Override
    public OrderTimeline add(OrderTimeline orderTimeline) {
        return orderTimelineRepository.save(orderTimeline);
    }
}
