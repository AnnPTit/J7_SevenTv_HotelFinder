package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.entity.Account;
import com.example.demo.entity.ComboUsed;
import com.example.demo.entity.InformationCustomer;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.OrderTimeline;
import com.example.demo.entity.Photo;
import com.example.demo.entity.Room;
import com.example.demo.entity.ServiceUsed;
import com.example.demo.service.ComboUsedService;
import com.example.demo.service.InformationCustomerService;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderService;
import com.example.demo.service.OrderTimelineService;
import com.example.demo.service.RoomService;
import com.example.demo.service.ServiceUsedSerivce;
import com.example.demo.util.BaseService;
import com.example.demo.util.DataUtil;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/order-detail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderTimelineService orderTimelineService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ServiceUsedSerivce serviceUsedSerivce;
    @Autowired
    private InformationCustomerService informationCustomerService;
    @Autowired
    private ComboUsedService comboUsedService;
    @Autowired
    private BaseService baseService;

    @GetMapping("/getList")
    public List<OrderDetail> getList() {
        return orderDetailService.getList();
    }

    @GetMapping("/loadOrderDetailByOrderId/{id}")
    public ResponseEntity<List<OrderDetailDTO>> loadOrderDetailByOrderId(@PathVariable("id") String id) {
        List<OrderDetailDTO> orderDetailDTOS = new ArrayList<>();
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(id);
//        System.out.println(orderDetails.toString());
        for (OrderDetail orderDetail : orderDetails) {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setId(orderDetail.getId());
            orderDetailDTO.setOrder(orderDetail.getOrder());
            orderDetailDTO.setRoom(orderDetail.getRoom());
            orderDetailDTO.setCheckIn(orderDetail.getCheckInDatetime());
            orderDetailDTO.setCheckOut(orderDetail.getCheckOutDatetime());
            orderDetailDTO.setCheckOutReal(orderDetail.getCheckOutDatetimeReal());
            orderDetailDTO.setCustomerQuantity(orderDetail.getCustomerQuantity());
            orderDetailDTO.setTimeIn(orderDetail.getTimeIn());
            orderDetailDTO.setRoomPrice(orderDetail.getRoomPrice());
            List<String> roomImages = orderDetail.getRoom().getPhotoList()
                    .stream()
                    .map(Photo::getUrl)
                    .collect(Collectors.toList());
            List<ServiceUsed> serviceUseds = serviceUsedSerivce.getAllByOrderDetailId(orderDetail.getId());
            List<InformationCustomer> informationCustomers = informationCustomerService.findAllByOrderDetailId(orderDetail.getId());
            List<ComboUsed> comboUseds = comboUsedService.getAllByOrderDetailId(orderDetail.getId());
            orderDetailDTO.setRoomImages(roomImages);
            orderDetailDTO.setServiceUsedList(serviceUseds);
            orderDetailDTO.setInformationCustomerList(informationCustomers);
            orderDetailDTO.setComboUsedList(comboUseds);
            orderDetailDTOS.add(orderDetailDTO);
        }
        return new ResponseEntity<List<OrderDetailDTO>>(orderDetailDTOS, HttpStatus.OK);
    }

    @GetMapping("/getBooking")
    public Integer getBooking() {
        return orderDetailService.getBooking();
    }

    @GetMapping("/getByIdOrder/{id}")
    public ResponseEntity<List<OrderDetail>> getByIdOrder(@PathVariable("id") String id) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(id);
        return new ResponseEntity<>(orderDetails, HttpStatus.OK);
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
    public ResponseEntity<?> save(
            @RequestBody OrderDetailDTO orderDetailDTO) throws ParseException {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formattedDate = currentDate.format(formatter);
        Random random = new Random();
        int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
        String orderDetailCode = "HDCT" + formattedDate + randomDigits;

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

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailCode(orderDetailCode);
        orderDetail.setRoom(orderDetailDTO.getRoom());
        orderDetail.setOrder(orderDetailDTO.getOrder());
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

        return new ResponseEntity<OrderDetail>(orderDetail, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OrderDetail> update(@PathVariable("id") String id, @RequestBody OrderDetailDTO orderDetailDTO) {
        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);

        Room room = orderDetail.getRoom();
        room.setStatus(Constant.ROOM.EMPTY);
        roomService.add(room);

        orderDetail.setRoom(orderDetailDTO.getRoom());
        orderDetail.setRoomPrice(orderDetailDTO.getRoomPrice());
        orderDetail.setNote(orderDetailDTO.getNote());
        orderDetail.setUpdateAt(new Date());
        orderDetail.setUpdatedBy(orderDetailDTO.getUpdatedBy());
        orderDetailService.add(orderDetail);

        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(orderDetail.getOrder());
        Account account = new Account();
        account.setId(baseService.getCurrentUser().getId());
        orderTimeline.setAccount(account);
        orderTimeline.setType(Constant.ORDER_TIMELINE.CHANGE_ROOM);
        orderTimeline.setNote(orderDetail.getNote());
        orderTimeline.setCreateAt(new Date());
        orderTimelineService.add(orderTimeline);

        Room updatedRoom = orderDetail.getRoom();
        updatedRoom.setStatus(Constant.ROOM.ACTIVE);
        roomService.add(updatedRoom);

        return new ResponseEntity<OrderDetail>(orderDetail, HttpStatus.OK);
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
        orderDetail.getRoom().setStatus(Constant.ROOM.EMPTY);
        List<ComboUsed> comboUsedList = comboUsedService.getAllByOrderDetailId(id);
        for (ComboUsed comboUsed : comboUsedList) {
            comboUsedService.delete(comboUsed);
        }
        List<ServiceUsed> serviceUsedList = serviceUsedSerivce.getAllByOrderDetailId(id);
        for (ServiceUsed serviceUsed : serviceUsedList) {
            serviceUsedSerivce.delete(serviceUsed);
        }
        List<InformationCustomer> informationCustomerList = informationCustomerService.findAllByOrderDetailId(id);
        for (InformationCustomer informationCustomer : informationCustomerList) {
            informationCustomerService.delete(informationCustomer);
        }
        orderDetail.setDeleted(baseService.getCurrentUser().getFullname());
        orderDetail.setStatus(Constant.ORDER_DETAIL.CANCEL);
        orderDetailService.add(orderDetail);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

}
