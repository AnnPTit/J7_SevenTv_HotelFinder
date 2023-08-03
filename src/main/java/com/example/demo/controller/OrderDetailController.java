package com.example.demo.controller;

import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.entity.OrderDetail;
import com.example.demo.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/order-detail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/loadByOrderId/{id}")
    public ResponseEntity<List<OrderDetailDTO>> loadByOrderId(@PathVariable("id") String id) {
        List<OrderDetailDTO> orderDetailDTOS = new ArrayList<>();
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(id);
        for (OrderDetail orderDetail : orderDetails) {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setId(orderDetail.getId());
            orderDetailDTO.setOrder(orderDetail.getOrder());
            orderDetailDTO.setRoom(orderDetail.getRoom());
            orderDetailDTO.setRoomPrice(orderDetail.getRoomPrice());
            orderDetailDTOS.add(orderDetailDTO);
        }
        return new ResponseEntity<List<OrderDetailDTO>> (orderDetailDTOS, HttpStatus.OK);
    }

    @GetMapping("/load")
    public List<OrderDetail> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        Page<OrderDetail> page = orderDetailService.getAll(pageable);
        return page.getContent();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<OrderDetail> detail(@PathVariable("id") String id) {
        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
        return new ResponseEntity<OrderDetail>(orderDetail, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<OrderDetail> save(@RequestBody OrderDetail orderDetail) {
        orderDetail.setCreateAt(new Date());
        orderDetail.setUpdateAt(new Date());
        orderDetailService.add(orderDetail);
        return new ResponseEntity<OrderDetail>(orderDetail, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OrderDetail> save(@PathVariable("id") String id, @RequestBody OrderDetail orderDetail) {
        orderDetail.setId(id);
        orderDetail.setUpdateAt(new Date());
        orderDetailService.add(orderDetail);
        return new ResponseEntity<OrderDetail>(orderDetail, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        orderDetailService.delete(id);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

}
