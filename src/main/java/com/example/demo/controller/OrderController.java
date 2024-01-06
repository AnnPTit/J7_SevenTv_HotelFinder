package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.ConfirmOrderDTO;
import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.dto.RevenueDTO;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.example.demo.util.BaseService;
import com.example.demo.util.DataUtil;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    @Autowired
    private DiscountProgramService discountProgramService;
    @Autowired
    private BaseService baseService;

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
                                           @RequestParam(name = "status", defaultValue = "") Integer status,
                                           @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return orderService.loadBookRoomOffline(key, status, pageable);
    }

    @GetMapping("/loadBookRoomOnline")
    public Page<Order> loadBookRoomOnline(@RequestParam(name = "key", defaultValue = "") String key,
                                          @RequestParam(name = "status", defaultValue = "") Integer status,
                                          @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        // Kiểm tra các hóa đơn hết hạn -> set trạng thái
        // Set thêm trạng thái cho hóa đơn chi tiết

        List<Order> listoOrders = orderService.getList();
        List<Order> ordersSave = new ArrayList<>();
        for (Order order : listoOrders) {
            // Nếu hạn thanh toán <= hôm nay
            Date date = new Date();
            if (order.getPaymentDeadline() != null && !order.getPaymentDeadline().after(date) && Constant.ORDER_STATUS.WAIT_CONFIRM.equals(order.getStatus())) {
                // Hết hạn thanh toán -> Set trạng thái hóa đơn về 8
                order.setStatus(Constant.ORDER_STATUS.EXPIRED_PAYMENT);
                for (OrderDetail orderDetail : order.getOrderDetailList()) {
                    orderDetail.setStatus(Constant.ORDER_DETAIL.EXPIRED_PAYMENT);
                    orderDetail.getRoom().setStatus(Constant.ROOM.EMPTY);
                    orderDetailService.add(orderDetail);
                }

                ordersSave.add(order);
            }

            // nếu ngày checkin lớn hơn ngày hôm nay
            Date bookingDateStart = order.getBookingDateStart();

            if (bookingDateStart != null) {
                // Lấy ngày hiện tại
                Instant now = Instant.now();
                LocalDate currentDate = now.atZone(ZoneId.systemDefault()).toLocalDate();

                // Chuyển đổi bookingDateStart thành LocalDate
                Instant bookingDateInstant = bookingDateStart.toInstant();
                LocalDate bookingDate = bookingDateInstant.atZone(ZoneId.systemDefault()).toLocalDate();

                // So sánh nếu bookingDate lớn hơn ngày hiện tại 1 ngày
                // 14                           12
                if (currentDate.isAfter(bookingDate.plusDays(1)) && Constant.ORDER_STATUS.WAIT_CONFIRM.equals(order.getStatus())) {
                    // Hết hạn thanh toán -> Set trạng thái hóa đơn về 9
                    order.setStatus(Constant.ORDER_STATUS.EXPIRED_CHECKIN);

                    for (OrderDetail orderDetail : order.getOrderDetailList()) {
                        orderDetail.setStatus(Constant.ORDER_DETAIL.EXPIRED_CHECKIN);
                        orderDetail.getRoom().setStatus(Constant.ROOM.EMPTY);
                        orderDetailService.add(orderDetail);
                    }

                    ordersSave.add(order);
                }
            }
        }
        orderService.add(ordersSave);

        Pageable pageable = PageRequest.of(current_page, 5);
        return orderService.loadBookRoomOnline(key, key, key, key, status, pageable);

    }

    @GetMapping("/loadByStatus")
    public Page<Order> getAllByStatus(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return orderService.getAllByStatus(pageable);
    }

    @GetMapping("/loadNotify")
    public List<Order> loadNotify() {
        return orderService.loadNotify();
    }

    @GetMapping("/getList")
    public List<Order> getList() {
        return orderService.getList();
    }

    @GetMapping("/getRevenue")
    public List<RevenueDTO> getRevenue() {
        return orderService.getRevenue();
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

    @GetMapping("/countByConfirmInfo")
    public Long countByConfirmInfo() {
        return orderService.countOrderConfirmInfo();
    }

    @GetMapping("/countByPaymentDeposit")
    public Long countByPaymentDeposit() {
        return orderService.countOrderPaymentDeposit();
    }

    @GetMapping("/getRevenueMonth")
    public BigDecimal getRevenueMonth() {
        return orderService.getRevenueMonth();
    }

    @GetMapping("/getRevenueYear")
    public BigDecimal getRevenueYear() {
        return orderService.getRevenueYear();
    }

    @GetMapping("/recommended/{orderId}")
    public ResponseEntity<ByteArrayResource> exportRecommended(@PathVariable("orderId") String orderId)
            throws JRException {
        return new ResponseEntity<>(orderService.exportRecommended(orderId), HttpStatus.OK);
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

    @GetMapping("/getByRoomId/{id}")
    public ResponseEntity<Order> getByRoomId(@PathVariable("id") String id) {
        Order order = orderService.getByRoomId(id);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody OrderDetailDTO orderDetailDTO) {
        Account account = accountService.findById(baseService.getCurrentUser().getId());
        Customer customer = customerService.getCustomertByCode();

        Date dayStart = orderDetailDTO.getCheckIn();
        Date dayEnd = orderDetailDTO.getCheckOut();
        System.out.println(dayStart);
        System.out.println(dayEnd);
        String id = orderDetailDTO.getRoom().getId();
        System.out.println(id);

        List<String> list = orderDetailService.checkRoomExist(DataUtil.dateToStringSql(dayStart), DataUtil.dateToStringSql(dayEnd), id);
        System.out.println(list.toString());
        if (!list.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String startDate = sdf.format(dayStart);
            String endDate = sdf.format(dayEnd);
            String errorMessage = "Phòng đã được đặt trong khoảng từ ngày " + startDate + " đến ngày " + endDate;
            return new ResponseEntity<String>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formattedDate = currentDate.format(formatter);
        Random random = new Random();
        int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
        String orderCode = "HD" + formattedDate + randomDigits;
        Order order = new Order();
        order.setOrderCode(orderCode);
        order.setTypeOfOrder(true);
        order.setTotalMoney(BigDecimal.valueOf(0));
        order.setDeposit(BigDecimal.valueOf(0));
        order.setSurcharge(BigDecimal.valueOf(0));
        order.setVat(BigDecimal.valueOf(0));
        order.setDiscount(BigDecimal.valueOf(0));
        order.setMoneyGivenByCustomer(BigDecimal.valueOf(0));
        order.setExcessMoney(BigDecimal.valueOf(0));
        order.setAccount(account);
        order.setCustomer(customer);
        order.setCreateAt(new Date());
        order.setCreateBy(account.getFullname());
        order.setUpdateAt(new Date());
        order.setUpdatedBy(account.getFullname());
        order.setStatus(Constant.ORDER_STATUS.WAIT_CONFIRM);
        order.setNote(account.getFullname() + " tạo hóa đơn");
        orderService.add(order);

        OrderDetail orderDetail = new OrderDetail();
        String orderDetailCode = "HDCT" + formattedDate + randomDigits;
        orderDetail.setOrderDetailCode(orderDetailCode);
        orderDetail.setRoom(orderDetailDTO.getRoom());
        orderDetail.setOrder(order);
        orderDetail.setCheckInDatetime(orderDetailDTO.getCheckIn());
        orderDetail.setCheckOutDatetime(orderDetailDTO.getCheckOut());
        orderDetail.setTimeIn(orderDetailDTO.getTimeIn());
        orderDetail.setRoomPrice(orderDetailDTO.getRoomPrice());
        orderDetail.setCustomerQuantity(orderDetailDTO.getCustomerQuantity());
        orderDetail.setCreateAt(new Date());
        orderDetail.setCreateBy(baseService.getCurrentUser().getFullname());
        orderDetail.setUpdateAt(new Date());
        orderDetail.setUpdatedBy(baseService.getCurrentUser().getFullname());
        orderDetail.setStatus(Constant.ORDER_DETAIL.WAIT_CONFIRM);
        orderDetailService.add(orderDetail);

        Room room = orderDetail.getRoom();
        room.setStatus(Constant.ROOM.ACTIVE);
        roomService.add(room);

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(Constant.ORDER_TIMELINE.WAIT_CONFIRM);
        orderTimeline.setNote(account.getFullname() + " tạo hóa đơn");
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);

        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Order> update(@PathVariable("id") String id, @RequestBody OrderDTO orderDTO) {
        Order order = orderService.getOrderById(id);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setVat(orderDTO.getVat());
        orderService.add(order);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PutMapping("/update-accept/{id}")
    public ResponseEntity<Order> updateStatus(@PathVariable("id") String id, @RequestBody OrderDTO orderDTO) {
        Customer customer = customerService.getCustomerById(orderDTO.getCustomerId());
        Account account = accountService.findById(baseService.getCurrentUser().getId());
        Order order = orderService.getOrderById(id);
        order.setCustomer(customer);
        order.setAccount(account);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setVat(orderDTO.getVat());
        order.setNote(orderDTO.getNote());
        order.setUpdateAt(new Date());
        order.setUpdatedBy(baseService.getCurrentUser().getFullname());
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
        Account account = accountService.findById(baseService.getCurrentUser().getId());
        order.setAccount(account);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setVat(orderDTO.getVat());
        order.setMoneyGivenByCustomer(orderDTO.getMoneyGivenByCustomer());
        order.setExcessMoney(orderDTO.getExcessMoney());
        order.setNote(orderDTO.getNote());
        order.setUpdateAt(new Date());
        order.setUpdatedBy(baseService.getCurrentUser().getFullname());
        if (orderDTO.getDiscountProgram() != null) {
            order.setDiscountProgram(orderDTO.getDiscountProgram());
            discountProgramService.updateNumberOfApplication(orderDTO.getDiscountProgram());
        }
        order.setStatus(Constant.ORDER_STATUS.CHECKED_OUT);
        order.setDiscount(orderDTO.getDiscountMoney());
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
        paymentMethod.setTotalMoney(order.getMoneyGivenByCustomer());
        paymentMethod.setNote(order.getNote());
        paymentMethod.setCreateAt(new Date());
        paymentMethod.setCreateBy(order.getCreateBy());
        paymentMethod.setUpdateAt(new Date());
        paymentMethod.setUpdatedBy(order.getUpdatedBy());
        paymentMethod.setStatus(Constant.COMMON_STATUS.ACTIVE);
        paymentMethodService.add(paymentMethod);

        HistoryTransaction historyTransaction = new HistoryTransaction();
        historyTransaction.setOrder(order);
        historyTransaction.setTotalMoney(order.getMoneyGivenByCustomer());
        historyTransaction.setNote(order.getNote());
        historyTransaction.setCreateAt(new Date());
        historyTransaction.setCreateBy(order.getCreateBy());
        historyTransaction.setUpdateAt(new Date());
        historyTransaction.setUpdatedBy(order.getUpdatedBy());
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
        Account account = accountService.findById(orderDTO.getAccount().getId());
        Customer customer = customerService.getCustomerById(orderDTO.getIdCustomerRepresent());

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
        order.setNote(orderDTO.getNote());
        order.setAccount(account);
        order.setCustomer(customer);
        order.setCreateAt(new Date());
        order.setCreateBy(account.getFullname());
        order.setUpdateAt(new Date());
        order.setUpdatedBy(account.getFullname());
        order.setStatus(Constant.ORDER_STATUS.WAIT_CONFIRM);
        orderService.add(order);

        List<OrderTimeline> orderTimelineList = orderTimelineService.getOrderTimelineByOrderId(orderDTO.getIdReturn());
        for (OrderTimeline orderTimeline : orderTimelineList) {
            orderTimeline.setAccount(account);
            orderTimelineService.add(orderTimeline);
        }

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(account);
        orderTimeline.setType(Constant.ORDER_TIMELINE.WAIT_CONFIRM);
        orderTimeline.setNote("Tạo hóa đơn");
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);

        Order or = orderService.getOrderById(orderDTO.getIdReturn());
        Customer customerRepresent = customerService.getCustomerById(orderDTO.getCustomerId());
        or.setTotalMoney(orderDTO.getTotalMoney());
        or.setVat(orderDTO.getVat());
        if (orderDTO.getCustomerId() != null) {
            or.setCustomer(customerRepresent);
        }
        orderService.add(or);

        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
        orderDetail.getRoom().setStatus(Constant.ROOM.ACTIVE);
        orderDetail.setOrder(order);
        orderDetail.setStatus(Constant.ORDER_DETAIL.CHECKED_IN);
        orderDetailService.add(orderDetail);

        OrderTimeline timeline = new OrderTimeline();
        timeline.setOrder(order);
        timeline.setAccount(account);
        timeline.setType(Constant.ORDER_TIMELINE.CHECKED_IN);
        timeline.setNote(order.getNote());
        timeline.setCreateAt(new Date());
        orderTimelineService.add(timeline);

        Order orderUpdate = orderService.getOrderById(order.getId());
        orderUpdate.setStatus(Constant.ORDER_STATUS.CHECKED_IN);
        orderService.add(orderUpdate);

        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id, @RequestBody OrderDTO orderDTO) {
        Order order = orderService.getOrderById(id);
        order.setStatus(Constant.ORDER_STATUS.CANCEL);
        order.setDeleted(baseService.getCurrentUser().getFullname());
        order.setNote(orderDTO.getNote());
        order.setUpdateAt(new Date());
        orderService.add(order);

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(id);
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setStatus(Constant.ORDER_DETAIL.CANCEL);
            orderDetailService.add(orderDetail);
            Room room = orderDetail.getRoom();
            room.setStatus(Constant.ROOM.EMPTY);
            roomService.add(room);
        }

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        Account account = accountService.findById(baseService.getCurrentUser().getId());
        orderTimeline.setAccount(account);
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

    @PutMapping("/update-checkout/{id}")
    public ResponseEntity<Order> updateSurcharge(@PathVariable("id") String id) {
        Order order = orderService.getOrderById(id);
        for (OrderDetail orderDetail : order.getOrderDetailList()) {
            if (orderDetail.getTimeIn() == 1) {
                Room room = roomService.getRoomById(orderDetail.getRoom().getId());
                orderDetail.setCheckOutDatetimeReal(new Date());
                Date bookingStart = orderDetail.getCheckInDatetime();
                Date bookingEnd = orderDetail.getCheckOutDatetimeReal();
                LocalDate startLocalDate = bookingStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endLocalDate = bookingEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                long days = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
                System.out.println(days);
                BigDecimal pricePerDay = room.getTypeRoom().getPricePerDay();
                BigDecimal totalCost = pricePerDay.multiply(BigDecimal.valueOf(days + 1));
                orderDetail.setRoomPrice(totalCost);
                orderDetail.setUpdateAt(new Date());
                orderDetailService.add(orderDetail);
            } else if (orderDetail.getTimeIn() == 2) {
                Room room = roomService.getRoomById(orderDetail.getRoom().getId());
                orderDetail.setCheckOutDatetimeReal(new Date());
                Date bookingStart = orderDetail.getCheckInDatetime();
                Date bookingEnd = orderDetail.getCheckOutDatetimeReal();
                LocalDateTime startLocalDateTime = bookingStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime endLocalDateTime = bookingEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                long hours = ChronoUnit.HOURS.between(startLocalDateTime, endLocalDateTime);
                BigDecimal pricePerHours = room.getTypeRoom().getPricePerHours();
                BigDecimal totalCost = pricePerHours.multiply(BigDecimal.valueOf(hours + 1));
                orderDetail.setRoomPrice(totalCost);
                orderDetail.setUpdateAt(new Date());
                orderDetailService.add(orderDetail);
            }
        }
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/update-surcharge")
    public ResponseEntity<ConfirmOrderDTO> updateSurcharge(@RequestBody ConfirmOrderDTO confirmOrderDTO) {
        return new ResponseEntity<>(orderService.updateSurcharge(confirmOrderDTO), HttpStatus.OK);
    }

    // TODO : Fake data đoạn này -> Truyền giá tiền và lấy ra  những CTGG thỏa mãn
    @GetMapping("/discount-program")
    private List<DiscountProgram> loadDiscount(@RequestParam("totalMoney") BigDecimal totalMoney) {
        List<DiscountProgram> discountProgramList = discountProgramService.loadDiscountByCondition();
        List<DiscountProgram> listFinal = new ArrayList<>();
        for (DiscountProgram discountProgram : discountProgramList) {
            if (discountProgram.getMinimumInvoice().compareTo(totalMoney) < 0) {
                listFinal.add(discountProgram);
            }
        }
        return listFinal;
    }

}
