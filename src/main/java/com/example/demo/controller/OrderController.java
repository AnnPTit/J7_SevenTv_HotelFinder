package com.example.demo.controller;

import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.entity.Account;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.OrderTimeline;
import com.example.demo.entity.Room;
import com.example.demo.service.AccountService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderService;
import com.example.demo.service.OrderTimelineService;
import com.example.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderTimelineService orderTimelineService;

    @GetMapping("/load")
    public Page<Order> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return orderService.getAll(pageable);
    }

    @GetMapping("/loadByStatus")
    public Page<Order> getAllByStatus(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return orderService.getAllByStatus(pageable);
    }

    @GetMapping("/getList")
    public List<Order> getList() {
        return orderService.getList();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Order> detail(@PathVariable("id") String id) {
        Order order = orderService.getOrderById(id);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Order> save(@RequestBody Order order) {
        Customer customer = customerService.getCustomerById();
        Account account = accountService.getAccountById();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formattedDate = currentDate.format(formatter);
        Random random = new Random();
        int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
        String orderCode = "HD" + formattedDate + randomDigits;
        order.setOrderCode(orderCode);
        order.setTypeOfOrder(true);
        order.setAccount(account);
        order.setCustomer(customer);
        order.setCreateAt(new Date());
        order.setUpdateAt(new Date());
        order.setStatus(1);
        orderService.add(order);

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(account);
        orderTimeline.setType(1);
        orderTimeline.setNote("Nhân viên tạo hóa đơn");
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);

        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

//    @PostMapping("/save-order")
//    public ResponseEntity<Order> saveOrder(@RequestBody OrderDetailDTO orderDetailDTO) {
//        Room room = roomService.getRoomById(orderDetailDTO.getRoomId());
//        Order order = new Order();
//        String ma = "HD00" + (orderService.getList().size() + 1);
//        order.setOrderCode(ma);
//        order.setCreateAt(new Date());
//        order.setUpdateAt(new Date());
//        order.setStatus(1);
//        orderService.add(order);
//
//        OrderDetail orderDetail = new OrderDetail();
//        String maHDCT = "HDCT00" + (orderDetailService.getList().size() + 1);
//        orderDetail.setOrder(order);
//        orderDetail.setRoom(room);
//        orderDetail.setOrderDetailCode(maHDCT);
//        orderDetail.setCreateAt(new Date());
//        orderDetail.setUpdateAt(new Date());
//        orderDetail.setStatus(1);
//        orderDetailService.add(orderDetail);
//
//        return new ResponseEntity<Order>(order, HttpStatus.OK);
//    }

    @PutMapping("/update-accept/{id}")
    public ResponseEntity<Order> save(@PathVariable("id") String id, @RequestBody Order order) {
        order.setId(id);
        order.setUpdateAt(new Date());
        order.setStatus(2);
        orderService.add(order);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        Order order = orderService.getOrderById(id);
        order.setStatus(0);
        orderService.add(order);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

}
