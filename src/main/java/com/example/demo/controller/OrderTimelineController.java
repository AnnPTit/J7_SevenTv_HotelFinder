package com.example.demo.controller;

import com.example.demo.dto.OrderTimelineDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderTimeline;
import com.example.demo.service.OrderService;
import com.example.demo.service.OrderTimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/order-timeline")
public class OrderTimelineController {

    @Autowired
    private OrderTimelineService orderTimelineService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/loadByOrderId/{id}")
    public ResponseEntity<List<OrderTimelineDTO>> getOrderTimelineByOrderId(@PathVariable("id") String id) {
        List<OrderTimelineDTO> orderTimelineDTOS = new ArrayList<>();
        List<OrderTimeline> orderTimelineList = orderTimelineService.getOrderTimelineByOrderId(id);
//        System.out.println(orderTimelineList.toString());

        for (OrderTimeline orderTimeline : orderTimelineList) {
            OrderTimelineDTO orderTimelineDTO = new OrderTimelineDTO();
            orderTimelineDTO.setId(orderTimeline.getId());
            orderTimelineDTO.setType(orderTimeline.getType());
            orderTimelineDTO.setNote(orderTimeline.getNote());
            orderTimelineDTO.setCreateAt(orderTimeline.getCreateAt());
            orderTimelineDTO.setOrder(orderTimeline.getOrder());
            orderTimelineDTO.setAccount(orderTimeline.getAccount());
            orderTimelineDTOS.add(orderTimelineDTO);
        }

        return new ResponseEntity<List<OrderTimelineDTO>>(orderTimelineDTOS, HttpStatus.OK);
    }

    @PostMapping("/update-status-2/{id}")
    public ResponseEntity<Order> updateStatus2(@PathVariable("id") String id,
                                               @RequestBody OrderTimelineDTO orderTimelineDTO) {
        Order order = orderService.getOrderById(id);
        try {

            if (order == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            order.setUpdateAt(new Date());
            order.setStatus(2);
            orderService.add(order);

            OrderTimeline orderTimeline = new OrderTimeline();
            orderTimeline.setCreateAt(new Date());
            orderTimeline.setOrder(order);
            orderTimeline.setAccount(order.getAccount());
            orderTimeline.setType(2);
            orderTimeline.setNote(orderTimelineDTO.getNote());
            orderTimelineService.add(orderTimeline);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PostMapping("/update-status-0/{id}")
    public ResponseEntity<Order> updateStatus0(@PathVariable("id") String id,
                                               @RequestBody OrderTimelineDTO orderTimelineDTO) {
        Order order = orderService.getOrderById(id);
        try {
            order.setUpdateAt(new Date());
            order.setStatus(0);
            orderService.add(order);

            OrderTimeline orderTimeline = new OrderTimeline();
            orderTimeline.setCreateAt(new Date());
            orderTimeline.setOrder(order);
            orderTimeline.setAccount(order.getAccount());
            orderTimeline.setType(0);
            orderTimeline.setNote(orderTimelineDTO.getNote());
            orderTimelineService.add(orderTimeline);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

}
