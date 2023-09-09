package com.example.demo.controller;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.Account;
import com.example.demo.entity.Customer;
import com.example.demo.entity.HistoryTransaction;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.OrderTimeline;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.entity.Room;
import com.example.demo.service.AccountService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.HistoryTransactionService;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderService;
import com.example.demo.service.OrderTimelineService;
import com.example.demo.service.PaymentMethodService;
import com.example.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    private PaymentMethodService paymentMethodService;
    @Autowired
    private HistoryTransactionService historyTransactionService;
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
        Account account = accountService.getAccountByCode();
        Customer customer = customerService.getCustomerByCode();

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
        order.setNote("Tạo đơn cho khách");
        orderService.add(order);

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(1);
        orderTimeline.setNote("Nhân viên tạo hóa đơn");
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);

        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Order> update(@PathVariable("id") String id, @RequestBody OrderDTO orderDTO) {
        Order order = orderService.getOrderById(id);
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderService.add(order);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PutMapping("/update-accept/{id}")
    public ResponseEntity<Order> updateStatus(@PathVariable("id") String id, @RequestBody OrderDTO orderDTO) {
        Order order = orderService.getOrderById(id);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setVat(orderDTO.getVat());
        order.setNote(orderDTO.getNote());
        order.setUpdateAt(new Date());
        order.setStatus(2);
        orderService.add(order);

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(order.getId());
        for (OrderDetail orderDetail : orderDetails) {
            Room room = orderDetail.getRoom();
            room.setStatus(2);
            roomService.add(room);
        }

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(2);
        orderTimeline.setNote(order.getNote());
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PutMapping("/update-return/{id}")
    public ResponseEntity<Order> updateReturn(@PathVariable("id") String id, @RequestBody OrderDTO orderDTO) {
        Order order = orderService.getOrderById(id);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setVat(orderDTO.getVat());
        order.setMoneyGivenByCustomer(orderDTO.getMoneyGivenByCustomer());
        order.setExcessMoney(orderDTO.getExcessMoney());
        order.setNote(orderDTO.getNote());
        order.setUpdateAt(new Date());
        order.setStatus(3);
        orderService.add(order);

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(order.getId());
        for (OrderDetail orderDetail : orderDetails) {
            Room room = orderDetail.getRoom();
            room.setStatus(1);
            roomService.add(room);
        }

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setOrder(order);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formattedDate = currentDate.format(formatter);
        Random random = new Random();
        int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
        String paymentMethodCode = "PT" + formattedDate + randomDigits;
        paymentMethod.setPaymentMethodCode(paymentMethodCode);
        paymentMethod.setMethod(true);
        paymentMethod.setTotalMoney(order.getTotalMoney());
        paymentMethod.setCreateAt(new Date());
        paymentMethod.setUpdateAt(new Date());
        paymentMethod.setStatus(1);
        paymentMethodService.add(paymentMethod);

        HistoryTransaction historyTransaction = new HistoryTransaction();
        historyTransaction.setOrder(order);
        historyTransaction.setTotalMoney(order.getTotalMoney());
        historyTransaction.setNote(order.getNote());
        historyTransaction.setCreateAt(new Date());
        historyTransaction.setUpdateAt(new Date());
        historyTransaction.setStatus(1);
        historyTransactionService.add(historyTransaction);

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(3);
        orderTimeline.setNote(order.getNote());
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        Order order = orderService.getOrderById(id);
        order.setStatus(0);
        order.setNote("Khách hủy hóa đơn");
        orderService.add(order);

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(id);
        for (OrderDetail orderDetail : orderDetails) {
            Room room = orderDetail.getRoom();
            room.setStatus(1);
            roomService.add(room);
        }

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(0);
        orderTimeline.setNote(order.getNote());
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

}
