package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.example.demo.service.ComboService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/home")
public class HomeController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private DepositService depositService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private TypeRoomService typeRoomService;
    @Autowired
    private ComboService comboService;
    @Autowired
    private HomeService homeService;

    @GetMapping("/room/loadAndSearch")
    public Page<Room> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                    @RequestParam(name = "floorId", defaultValue = "") String floorId,
                                    @RequestParam(name = "typeRoomId", defaultValue = "") String typeRoomId,
                                    @RequestParam(name = "id", defaultValue = "") String id,
                                    @RequestParam(name = "current_page", defaultValue = "0") int current_page
    ) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.loadAndSearchForHome(key, key, floorId, typeRoomId, id, pageable);
    }

    @GetMapping("/room/loadAndSearch1")
    public Page<Room> loadAndSearch1(@RequestParam(name = "key", defaultValue = "") String key,
                                     @RequestParam(name = "floorId", defaultValue = "") String floorId,
                                     @RequestParam(name = "typeRoomId", defaultValue = "") String typeRoomId,
                                     @RequestParam(name = "current_page", defaultValue = "0") int current_page
    ) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.loadAndSearch(key, key, floorId, typeRoomId, pageable);
    }

    @GetMapping("/room/load")
    public Page<Room> loadRoom(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 6);
        return roomService.getAllByStatus(Constant.COMMON_STATUS.ACTIVE, pageable);
    }

    @GetMapping("/room/loadByBook")
    public Page<Room> getRoomByVBooking(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.getAllByStatus(Constant.COMMON_STATUS.ACTIVE, pageable);
    }

    @GetMapping("/room/detail/{id}")
    public ResponseEntity<Room> detail(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    // Deposit
    @GetMapping("/deposit/getByCode")
    public ResponseEntity<Deposit> getByCode(@RequestParam("code") String code) {

        if (depositService.getByCode(code) != null) {
            Deposit deposit = depositService.getByCode(code);

            return new ResponseEntity<Deposit>(deposit, HttpStatus.OK);
        } else {
            return new ResponseEntity("Khong tim thay", HttpStatus.NOT_FOUND);
        }
    }

    // Service
    @GetMapping("/service/getAll")
    public List<Service> getAll() {
        return serviceService.getAll();
    }

    // Customer
    @GetMapping("/customer/{code}")
    public ResponseEntity<Customer> findByCode(@PathVariable("code") String code) {
        try {
            return new ResponseEntity<Customer>(customerService.findCustomerByCode(code), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("Khong tim thay " + code, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/customer/save")
    public ResponseEntity<Customer> add( @RequestBody Customer customer,
                                        BindingResult result) {
        customer.setCustomerCode(customerService.generateCustomerCode());
        customer.setCreateAt(new Date());
        customer.setUpdateAt(new Date());
        customer.setStatus(1);

        customerService.add(customer);
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    // Filter
    @GetMapping("/get-room-filter")
    public List<Room> getRoomsByFilters(
            @RequestParam(name = "roomName", defaultValue = "") String roomName,
            @RequestParam(name = "typeRoomCode", defaultValue = "") String typeRoomCode,
            @RequestParam(name = "startPrice", defaultValue = "0") BigDecimal startPrice,
            @RequestParam(name = "endPrice", defaultValue = "20000000000000") BigDecimal endPrice,
            @RequestParam(name = "capacity", defaultValue = "1") Integer capacity,
            @RequestParam(name = "dayStart", defaultValue = "") Date dayStart,
            @RequestParam(name = "dayEnd", defaultValue = "") Date dayEnd
    ) {
        List<Room> roomList = roomService.findRoomsByFilters(
                roomName,
                typeRoomCode,
                startPrice,
                endPrice,
                capacity,
                dayStart,
                dayEnd
        );
        return roomList;
    }

    @GetMapping("/type-room/getList")
    public List<TypeRoom> getList() {
        return typeRoomService.getList();
    }


    @PostMapping("/room/search")
    public Page<RoomResponeDTO> testSearch(@RequestBody RoomRequestDTO roomRequestDTO,
                                           @RequestParam(name = "current_page", defaultValue = "0") int current_page,
                                           @RequestParam(name = "total_page", defaultValue = "1000") int total_page) {
        Pageable pageable = PageRequest.of(current_page, total_page);
        return roomService.search(roomRequestDTO, pageable);
    }

    @GetMapping("/room/top")
    public Page<RoomResponeDTO> testSearch(@RequestParam(name = "current_page", defaultValue = "0") int current_page,
                                           @RequestParam(name = "total_page", defaultValue = "1000") int total_page) {
        Pageable pageable = PageRequest.of(current_page, total_page);
        return roomService.topBook(pageable);
    }

    @GetMapping("/combo/getall")
    public List<ComboDTO> getAllCombo() {
        return comboService.getAll();
    }

    @GetMapping("/cart/{customId}/{odStt}")
    public List<CartDTO> getCart(@PathVariable("customId") String customId, @PathVariable("odStt") Integer odStt) {
        return roomService.getCart(customId, odStt);
    }

    @PostMapping("/login")
    public ResponseEntity<Customer> login(@RequestBody CustomerLoginDTO customerLoginDTO) {
        return new ResponseEntity<>(customerService.login(customerLoginDTO), HttpStatus.OK);
    }

    @PostMapping("/order/cancel/{code}/{oddStt}")
    public ResponseEntity<Message> cancelOrder(@PathVariable("code") String code,
                                               @PathVariable("oddStt") Integer oddStt,
                                               @RequestParam( name ="refuseReason" , defaultValue = "") String refuseReason) {
        return new ResponseEntity<>(homeService.cancelOrder(code, oddStt,refuseReason), HttpStatus.OK);
    }


}
