package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.ConfirmOrderDTO;
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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/order")
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

    @GetMapping("/loadAndSearch")
    public Page<Order> loadAndSearch(
            @RequestParam(name = "key", defaultValue = "") String key,
            @RequestParam(name = "typeOfOrder", defaultValue = "") Boolean typeOfOrder,
            @RequestParam(name = "status", defaultValue = "") Integer status,
            @RequestParam(name = "startDate", defaultValue = "") String startDateStr,
            @RequestParam(name = "endDate", defaultValue = "") String endDateStr,
            @RequestParam(name = "current_page", defaultValue = "0") int current_page) {

        Date startDate = null;
        Date endDate = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (!startDateStr.isEmpty()) {
                startDate = dateFormat.parse(startDateStr);
            }

            if (!endDateStr.isEmpty()) {
                endDate = dateFormat.parse(endDateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Pageable pageable = PageRequest.of(current_page, 5);
        return orderService.loadAndSearch(key, typeOfOrder, status, key, startDate, endDate, pageable);
    }

    @GetMapping("/loadBookRoomOffline")
    public Page<Order> loadBookRoomOffline(@RequestParam(name = "key", defaultValue = "") String key,
                                           @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return orderService.loadBookRoomOffline(key, pageable);
    }

    @GetMapping("/loadBookRoomOnline")
    public Page<Order> loadBookRoomOnline(@RequestParam(name = "key", defaultValue = "") String key,
                                          @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return orderService.loadBookRoomOnline(key, pageable);
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

    @GetMapping("/countByCancel")
    public Long countByCancel() {
        return orderService.countOrderCancel();
    }

    @GetMapping("/countByWait")
    public Long countByWait() {
        return orderService.countOrderWait();
    }

    @GetMapping("/countByConfirm")
    public Long countByConfirm() {
        return orderService.countOrderConfirm();
    }

    @GetMapping("/countByAccept")
    public Long countByAccept() {
        return orderService.countOrderAccept();
    }

    @GetMapping("/getRevenueMonth")
    public BigDecimal getRevenueMonth() {
        return orderService.getRevenueMonth();
    }

    @GetMapping("/getRevenueYear")
    public BigDecimal getRevenueYear() {
        return orderService.getRevenueYear();
    }

    @GetMapping("/detail-info/{id}")
    public ResponseEntity<?> detailInfo(@PathVariable("id") String id) {
        Order order = orderService.getOrderById(id);
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setTotalMoney(order.getTotalMoney());
        orderDTO.setDeposit(order.getDeposit());
        orderDTO.setVat(order.getVat());
        orderDTO.setMoneyGivenByCustomer(order.getMoneyGivenByCustomer());
        orderDTO.setNote(order.getNote());
        orderDTO.setBookingDateStart(order.getBookingDateStart());
        orderDTO.setBookingEndStart(order.getBookingDateEnd());
        orderDTO.setCustomer(order.getCustomer());
        orderDTO.setAccount(order.getAccount());
        orderDTO.setOrderDetailList(order.getOrderDetailList());
        orderDTO.setStatus(order.getStatus());
        return new ResponseEntity<OrderDTO>(orderDTO, HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Order> detail(@PathVariable("id") String id) {
        Order order = orderService.getOrderById(id);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Order> save(@RequestBody Order order) {
        Account account = accountService.getAccountByCode();
        Customer customer = customerService.getCustomertByCode();

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formattedDate = currentDate.format(formatter);
        Random random = new Random();
        int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
        String orderCode = "HD" + formattedDate + randomDigits;
        order.setOrderCode(orderCode);
        order.setTypeOfOrder(true);
        order.setTotalMoney(BigDecimal.valueOf(0));
        order.setDeposit(BigDecimal.valueOf(0));
        order.setSurcharge(BigDecimal.valueOf(0));
        order.setDiscount(BigDecimal.valueOf(0));
        order.setAccount(account);
        order.setCustomer(customer);
        order.setCreateAt(new Date());
        order.setUpdateAt(new Date());
        order.setStatus(Constant.ORDER_STATUS.WAIT_CONFIRM);
        order.setNote("Tạo hóa đơn");
        orderService.add(order);

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(Constant.ORDER_TIMELINE.WAIT_CONFIRM);
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
        Customer customer = customerService.getCustomerById(orderDTO.getCustomerId());
        Order order = orderService.getOrderById(id);
        order.setCustomer(customer);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setVat(orderDTO.getVat());
        order.setNote(orderDTO.getNote());
        order.setUpdateAt(new Date());
        order.setStatus(Constant.ORDER_STATUS.CHECKED_IN);
        orderService.add(order);

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(order.getId());
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setStatus(Constant.ORDER_DETAIL.CHECKED_IN);
            orderDetailService.add(orderDetail);
            Room room = orderDetail.getRoom();
            room.setStatus(Constant.ROOM.ACTIVE);
            roomService.add(room);
        }

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(Constant.ORDER_TIMELINE.CHECKED_IN);
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
        order.setStatus(Constant.ORDER_STATUS.CHECKED_OUT);
        orderService.add(order);

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(order.getId());
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setStatus(Constant.ORDER_DETAIL.CHECKED_OUT);
            orderDetailService.add(orderDetail);
            Room room = orderDetail.getRoom();
            room.setStatus(Constant.ROOM.EMPTY);
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
        paymentMethod.setNote(order.getNote());
        paymentMethod.setCreateAt(new Date());
        paymentMethod.setUpdateAt(new Date());
        paymentMethod.setStatus(Constant.COMMON_STATUS.ACTIVE);
        paymentMethodService.add(paymentMethod);

        HistoryTransaction historyTransaction = new HistoryTransaction();
        historyTransaction.setOrder(order);
        historyTransaction.setTotalMoney(order.getTotalMoney());
        historyTransaction.setNote(order.getNote());
        historyTransaction.setCreateAt(new Date());
        historyTransaction.setUpdateAt(new Date());
        historyTransaction.setStatus(Constant.COMMON_STATUS.ACTIVE);
        historyTransactionService.add(historyTransaction);

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(Constant.ORDER_TIMELINE.CHECKED_OUT);
        orderTimeline.setNote(order.getNote());
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PostMapping("/return/{id}")
    public ResponseEntity<Order> returnRoom(@PathVariable("id") String id, @RequestBody OrderDTO orderDTO) {
        Account account = accountService.getAccountByCode();
        Customer customer = customerService.getCustomerById(orderDTO.getCustomerId());

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formattedDate = currentDate.format(formatter);
        Random random = new Random();
        int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
        String orderCode = "HD" + formattedDate + randomDigits;
        Order order = new Order();
        order.setOrderCode(orderCode);
        order.setTypeOfOrder(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setVat(orderDTO.getVat());
        order.setMoneyGivenByCustomer(orderDTO.getMoneyGivenByCustomer());
        order.setExcessMoney(orderDTO.getExcessMoney());
        order.setNote(orderDTO.getNote());
        order.setAccount(account);
        order.setCustomer(customer);
        order.setCreateAt(new Date());
        order.setUpdateAt(new Date());
        order.setStatus(Constant.ORDER_STATUS.WAIT_CONFIRM);
        orderService.add(order);

        Order or = orderService.getOrderById(orderDTO.getIdReturn());
        or.setTotalMoney(or.getTotalMoney().subtract(orderDTO.getTotalMoney()));
        orderService.add(or);

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(Constant.ORDER_TIMELINE.WAIT_CONFIRM);
        orderTimeline.setNote("Nhân viên tạo hóa đơn");
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);

        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
        orderDetail.getRoom().setStatus(Constant.ROOM.EMPTY);
        orderDetail.setOrder(order);
        orderDetail.setStatus(Constant.ORDER_DETAIL.CHECKED_OUT);
        orderDetailService.add(orderDetail);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setOrder(order);
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formatDate = localDate.format(dateTimeFormatter);
        Random randomPayment = new Random();
        int randomCode = randomPayment.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
        String paymentMethodCode = "PT" + formatDate + randomCode;
        paymentMethod.setPaymentMethodCode(paymentMethodCode);
        paymentMethod.setMethod(true);
        paymentMethod.setTotalMoney(order.getTotalMoney());
        paymentMethod.setNote(order.getNote());
        paymentMethod.setCreateAt(new Date());
        paymentMethod.setUpdateAt(new Date());
        paymentMethod.setStatus(Constant.COMMON_STATUS.ACTIVE);
        paymentMethodService.add(paymentMethod);

        HistoryTransaction historyTransaction = new HistoryTransaction();
        historyTransaction.setOrder(order);
        historyTransaction.setTotalMoney(order.getTotalMoney());
        historyTransaction.setNote(order.getNote());
        historyTransaction.setCreateAt(new Date());
        historyTransaction.setUpdateAt(new Date());
        historyTransaction.setStatus(Constant.COMMON_STATUS.ACTIVE);
        historyTransactionService.add(historyTransaction);

        OrderTimeline timeline = new OrderTimeline();
        timeline.setOrder(order);
        timeline.setAccount(order.getAccount());
        timeline.setType(Constant.ORDER_TIMELINE.LEAVE_EARLY);
        timeline.setNote(order.getNote());
        timeline.setCreateAt(new Date());
        orderTimelineService.add(timeline);

        Order orderUpdate = orderService.getOrderById(order.getId());
        orderUpdate.setStatus(Constant.ORDER_STATUS.CHECKED_OUT);
        orderService.add(orderUpdate);

        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        Order order = orderService.getOrderById(id);
        order.setStatus(Constant.ORDER_STATUS.CANCEL);
        order.setNote("Hủy hóa đơn");
        orderService.add(order);

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(id);
        for (OrderDetail orderDetail : orderDetails) {
            Room room = orderDetail.getRoom();
            room.setStatus(Constant.ROOM.EMPTY);
            roomService.add(room);
        }

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(Constant.ORDER_TIMELINE.CANCEL);
        orderTimeline.setNote(order.getNote());
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

    @PostMapping("/confirm-order")
    public ResponseEntity<ConfirmOrderDTO> confirmOrder(@RequestBody ConfirmOrderDTO confirmOrderDTO) {
        return new ResponseEntity<>(orderService.confirmOrder(confirmOrderDTO), HttpStatus.OK);
    }


}
