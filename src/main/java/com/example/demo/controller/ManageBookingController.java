package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.AddRoomDTO;
import com.example.demo.dto.CustomerBookingDTO;
import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Customer;
import com.example.demo.entity.InformationCustomer;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Room;
import com.example.demo.service.BookingService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.InformationCustomerService;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderService;
import com.example.demo.util.BaseService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/manage-booking")
public class ManageBookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private InformationCustomerService informationCustomerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private BaseService baseService;

    @GetMapping("/load")
    public Page<Booking> findAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return bookingService.findAll(pageable);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Booking> getById(@PathVariable("id") String id) {
        Booking booking = bookingService.getById(id);
        return new ResponseEntity<Booking>(booking, HttpStatus.OK);
    }

    @DeleteMapping("/change-status/{id}")
    public ResponseEntity<String> changeStatus(@PathVariable("id") String id) {
        Booking booking = bookingService.getById(id);
        booking.setStatus(Constant.MANAGE_BOOKING.ACTIVE);
        bookingService.update(booking);
        return new ResponseEntity<String>("Success " + id + " successfully", HttpStatus.OK);
    }

    @DeleteMapping("/change-wait-room/{id}")
    public ResponseEntity<String> changeWaitRoom(@PathVariable("id") String id) {
        Booking booking = bookingService.getById(id);
        booking.setStatus(Constant.MANAGE_BOOKING.WAIT_ROOM);
        bookingService.update(booking);
        return new ResponseEntity<String>("Success " + id + " successfully", HttpStatus.OK);
    }

    @DeleteMapping("/change-cancel/{id}")
    public ResponseEntity<String> changeCancel(@PathVariable("id") String id) {
        Booking booking = bookingService.getById(id);
        booking.setStatus(Constant.MANAGE_BOOKING.UNACTIVE);
        bookingService.update(booking);
        return new ResponseEntity<String>("Success " + id + " successfully", HttpStatus.OK);
    }

    @PostMapping("/accept-customer")
    public ResponseEntity<?> acceptCustomer(@RequestBody CustomerBookingDTO customerBookingDTO) {
        Customer existingCustomer = customerService.findByCitizenId(customerBookingDTO.getCitizenId());

        if (existingCustomer != null) {
            return new ResponseEntity<String>("Giấy tờ tùy thân đã tồn tại.", HttpStatus.BAD_REQUEST);
        }

        Customer customer = customerService.getCustomerById(customerBookingDTO.getIdCustomer());
        customer.setCitizenId(customerBookingDTO.getCitizenId());
        customer.setBirthday(customerBookingDTO.getBirthday());
        customer.setGender(customerBookingDTO.getGender());
        customer.setNationality(customerBookingDTO.getNationality());
        customer.setAddress(customerBookingDTO.getAddress());
        customerService.add(customer);

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(customerBookingDTO.getIdOrder());
        addInformationCustomer(customer, orderDetails);

        Booking booking = bookingService.getById(customerBookingDTO.getIdBooking());
        booking.setStatus(Constant.MANAGE_BOOKING.CHECKED_IN);
        bookingService.update(booking);

        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    public void addInformationCustomer(Customer customer, List<OrderDetail> orderDetails) {
        InformationCustomer informationCustomer = new InformationCustomer();
        informationCustomer.setFullname(customer.getFullname());
        informationCustomer.setEmail(customer.getEmail());
        informationCustomer.setPhoneNumber(customer.getPhoneNumber());
        informationCustomer.setCitizenId(customer.getCitizenId());
        informationCustomer.setBirthday(customer.getBirthday());
        informationCustomer.setGender(customer.getGender());
        informationCustomer.setNationality(customer.getNationality());
        informationCustomer.setAddress(customer.getAddress());
        informationCustomer.setCreateAt(new Date());
        informationCustomer.setUpdateAt(new Date());
        informationCustomer.setStatus(Constant.COMMON_STATUS.ACTIVE);
        informationCustomer.setOrderDetail(orderDetails.get(0));
        informationCustomerService.add(informationCustomer);
    }

}
