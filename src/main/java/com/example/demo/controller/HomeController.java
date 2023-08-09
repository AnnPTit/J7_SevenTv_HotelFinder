package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Deposit;
import com.example.demo.entity.Room;
import com.example.demo.entity.Service;
import com.example.demo.service.CustomerService;
import com.example.demo.service.DepositService;
import com.example.demo.service.RoomService;
import com.example.demo.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public Page<Room> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                    @RequestParam(name = "floorId", defaultValue = "") String floorId,
                                    @RequestParam(name = "typeRoomId", defaultValue = "") String typeRoomId,
                                    @RequestParam(name = "current_page", defaultValue = "0") int current_page
    ) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.loadAndSearch(key, key, floorId, typeRoomId, pageable);
    }

    @GetMapping("/room/detail/{id}")
    public ResponseEntity<Room> detail(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    //    Deposit
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
            return new ResponseEntity<>(customerService.findByCustomerCode(code), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("Khong tim thay " + code, HttpStatus.NOT_FOUND);
        }
    }

}
