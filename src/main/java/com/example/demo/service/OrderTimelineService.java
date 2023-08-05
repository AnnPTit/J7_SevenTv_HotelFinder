package com.example.demo.service;

import com.example.demo.dto.OrderTimelineDTO;
import com.example.demo.entity.OrderTimeline;

import java.util.List;

public interface OrderTimelineService {

    List<OrderTimeline> getAll();

    List<OrderTimeline> getOrderTimelineByOrderId(String id);

    OrderTimeline add(OrderTimeline orderTimeline);

}
