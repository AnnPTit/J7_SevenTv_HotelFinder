package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.RevenueDTO;
import com.example.demo.entity.Account;
import com.example.demo.entity.Combo;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Floor;
import com.example.demo.entity.Room;
import com.example.demo.entity.Service;
import com.example.demo.entity.ServiceType;
import com.example.demo.entity.TypeRoom;
import com.example.demo.entity.Unit;
import com.example.demo.repository.ComboRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.ComboService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.FloorService;
import com.example.demo.service.OrderService;
import com.example.demo.service.RoomService;
import com.example.demo.service.ServiceService;
import com.example.demo.service.ServiceTypeService;
import com.example.demo.service.TypeRoomService;
import com.example.demo.service.UnitService;
import com.example.demo.util.ExcelExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/general")
public class ApiGeneralController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private ComboService comboService;
    @Autowired
    private FloorService floorService;
    @Autowired
    private TypeRoomService typeRoomService;
    @Autowired
    private ServiceTypeService serviceTypeService;
    @Autowired
    private UnitService unitService;
    @Autowired
    private ComboRepository comboRepository;

    @GetMapping("/unit/getAll")
    public List<Unit> findAllUnit() {
        return unitService.findAll();
    }

    @GetMapping("/service-type/getAll")
    public List<ServiceType> findAllServiceType(){
        return serviceTypeService.findAll();
    }

    @GetMapping("/type-room/getList")
    public List<TypeRoom> getListTypeRoom() {
        return typeRoomService.getList();
    }

    @GetMapping("/floor/getList")
    public List<Floor> getListFloor() {
        return floorService.getList();
    }

    @GetMapping("/room/loadAndSearchBookRoom")
    public List<Room> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                    @RequestParam(name = "floorId", defaultValue = "") String floorId,
                                    @RequestParam(name = "typeRoomId", defaultValue = "") String typeRoomId,
                                    @RequestParam(name = "start", defaultValue = "0") BigDecimal start,
                                    @RequestParam(name = "end", defaultValue = "100000000") BigDecimal end,
                                    @RequestParam(name = "dayStart", defaultValue = "") String dayStart,
                                    @RequestParam(name = "dayEnd", defaultValue = "") String dayEnd) {
        Date startDay = null;
        Date endDay = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (!dayStart.isEmpty()) {
                startDay = dateFormat.parse(dayStart);
            }

            if (!dayEnd.isEmpty()) {
                endDay = dateFormat.parse(dayEnd);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return roomService.loadAndSearchBookRoom(key, key, floorId, typeRoomId, start, end, startDay, endDay);
    }

    @GetMapping("/customer/getAllByOrderId/{id}")
    public List<Customer> findAll(@PathVariable("id") String id) {
        return customerService.getAllCustomer(id);
    }

    @GetMapping("/service/detail/{id}")
    public ResponseEntity<Service> detailService(@PathVariable("id") String id) {
        Service service = serviceService.findById(id);
        return new ResponseEntity<Service>(service, HttpStatus.OK);
    }

    @GetMapping("/combo/detail/{id}")
    public ResponseEntity<Combo> detailCombo(@PathVariable("id") String id) {
        Combo combo = comboService.findById(id);
        return new ResponseEntity<Combo>(combo, HttpStatus.OK);
    }

    @GetMapping("/combo/getAll")
    public List<Combo> getAllCombo() {
        return comboRepository.getAll();
    }

    @GetMapping("/customer/getAllByOrderDetailId/{id}")
    public List<Customer> findAllByOrderDetail(@PathVariable("id") String id) {
        return customerService.getAllCustomerByOrderDetailId(id);
    }

    @GetMapping("/room/loadByCondition")
    public List<Room> loadByCondition(@RequestParam(name = "typeRoomId", defaultValue = "") String typeRoomId,
                                      @RequestParam(name = "capacity", defaultValue = "0") Integer capacity,
                                      @RequestParam(name = "adult", defaultValue = "0") Integer adult,
                                      @RequestParam(name = "children", defaultValue = "0") Integer children,
                                      @RequestParam(name = "dayStart", defaultValue = "") String dayStart,
                                      @RequestParam(name = "dayEnd", defaultValue = "") String dayEnd) {
        Date startDay = null;
        Date endDay = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (!dayStart.isEmpty()) {
                startDay = dateFormat.parse(dayStart);
            }

            if (!dayEnd.isEmpty()) {
                endDay = dateFormat.parse(dayEnd);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return roomService.loadRoomByCondition(typeRoomId, capacity, adult, children, startDay, endDay);
    }

    @GetMapping("/service/getAll")
    public List<Service> getAll() {
        return serviceService.getAll();
    }

    @GetMapping("/room/getList")
    public List<Room> getList() {
        return roomService.getList();
    }

    @GetMapping("/account/getAll")
    public List<Account> findAllAccount() {
        return accountService.findAll();
    }

    @GetMapping("/customer/getAll")
    public List<Customer> findAllWithSearch(@RequestParam(name = "key", defaultValue = "") String key) {
        return customerService.findAllByStatus(key, key, key);
    }

    @GetMapping("/getCustomerDifferenceOrder/{orderId}/{orderDetailId}")
    public List<Customer> getCustomerDifferentOrder(@PathVariable("orderId") String orderId,
                                                    @PathVariable("orderDetailId") String orderDetailId) {
        return customerService.getCustomerDifferentOrder(orderId, orderDetailId);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<String> changeStatus(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        room.setStatus(Constant.ROOM.EMPTY);
        roomService.add(room);
        return new ResponseEntity<String>("Success " + id + " successfully", HttpStatus.OK);
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel() {
        List<RevenueDTO> revenueList = orderService.getRevenue();
        byte[] excelBytes;

        try {
            excelBytes = ExcelExporter.exportToExcel(revenueList);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "doanh_thu.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }

}
