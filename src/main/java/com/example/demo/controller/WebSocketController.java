package com.example.demo.controller;

import com.example.demo.dto.PayloadObject;
import com.example.demo.dto.RoomData;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final CustomerService customerService;

    private final RoomService roomService;

    private final OrderService orderService;

    private final OrderTimelineService orderTimelineService;

    private final OrderDetailService orderDetailService;


    @MessageMapping("/products")
    @SendTo("/topic/product")
    public String broadcastNews(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PayloadObject payload = objectMapper.readValue(message, PayloadObject.class);

            System.out.println(payload);
            Random random = new Random();
            int randomNumber = random.nextInt(1000);
            // TODO: Tạo hóa đơn
            // B1 : Get khách hàng theo email -> Nếu chưa có tạo mới khách hàng
            Customer customer = customerService.findCustomerByEmail(payload.getUser().getEmail()).orElse(null);
            if (customer == null) {
                // TODO : tạo khách hàng
                String customerCode = "KH" + randomNumber;
                Customer newCustomer = new Customer();
                newCustomer.setCustomerCode(customerCode);
                newCustomer.setUsername(customerCode);
                newCustomer.setBirthday(new Date());
                newCustomer.setAddress("Deo biet");
                newCustomer.setStatus(1);
                newCustomer.setCreateAt(new Date());
                newCustomer.setEmail(payload.getUser().getEmail());
                newCustomer.setFullname(payload.getUser().getHoVaTen());
                newCustomer.setPassword(customerCode + "12345");
                newCustomer.setPhoneNumber(payload.getUser().getSoDienThoai());
                customerService.add(newCustomer);
            }
            // B2 : Lấy danh sách phòng


            // B3 : Tạo hóa đơn
            Order order = new Order();
            Customer customerForOrder = customerService.findCustomerByEmail(payload.getUser().getEmail()).orElse(null);
            String orderCode = "HD" + randomNumber;
            order.setCustomer(customerForOrder);
            order.setOrderCode(orderCode);
            order.setTypeOfOrder(false);
            order.setDeposit(payload.getDeposit());
            order.setBookingDateStart(payload.getDayStart());
            order.setBookingDateEnd(payload.getDayEnd());
            order.setTotalMoney(payload.getTotalPriceRoom());
            order.setCreateAt(new Date());
            order.setUpdateAt(new Date());
            order.setNote(payload.getNote());
            order.setStatus(1);
            orderService.add(order);
            // B4 tạo Order timeline
            OrderTimeline orderTimeline = new OrderTimeline();
            orderTimeline.setOrder(order);
            orderTimeline.setType(1);
            orderTimeline.setNote("Khách hàng tạo hóa đơn" + payload.getUser().getEmail());
            orderTimeline.setCreateAt(new Date());
            orderTimelineService.add(orderTimeline);
            // B5 : Tạo hóa đơn chi tiết
            for (RoomData roomData : payload.getRooms()
            ) {
                Room room = roomService.getRoomById(roomData.getId());
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setRoom(room);
                orderDetail.setOrder(order);
                orderDetail.setOrderDetailCode("HDCT" + randomNumber);
                orderDetail.setRoomPrice(room.getTypeRoom().getPricePerDay());
                orderDetail.setCustomerQuantity(roomData.getGuestCount());
                orderDetail.setCreateAt(new Date());
                orderDetail.setUpdateAt(new Date());
                orderDetail.setStatus(1);
                // B6 : Sửa lại trạng thái cho phòng
                room.setStatus(2);
                roomService.add(room);
                // Thêm hóa đơn chi tiết
                orderDetailService.add(orderDetail);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
