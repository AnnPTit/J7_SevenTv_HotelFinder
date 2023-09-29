package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.InformationCustomerDTO;
import com.example.demo.entity.Customer;
import com.example.demo.entity.InformationCustomer;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.service.CustomerService;
import com.example.demo.service.InformationCustomerService;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/information-customer")
public class InformationCustomerController {

    @Autowired
    private InformationCustomerService informationCustomerService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/load/{id}")
    public List<InformationCustomer> loadByOrderId(@PathVariable("id") String id) {
        return informationCustomerService.findAllByOrderDetailId(id);
    }

    @PostMapping("/save/{id}")
    public ResponseEntity<InformationCustomer> save(@PathVariable("id") String id,
                                                    @RequestBody InformationCustomerDTO informationCustomerDTO) {
        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
        InformationCustomer informationCustomer = new InformationCustomer();
        informationCustomer.setOrderDetail(orderDetail);
        informationCustomer.setFullname(informationCustomerDTO.getFullname());
        informationCustomer.setGender(informationCustomerDTO.getGender());
        informationCustomer.setBirthday(informationCustomerDTO.getBirthday());
        informationCustomer.setPhoneNumber(informationCustomerDTO.getPhoneNumber());
        informationCustomer.setCitizenId(informationCustomerDTO.getCitizenId());
        informationCustomer.setStayFrom(orderDetail.getCheckInDatetime());
        informationCustomer.setStayTo(orderDetail.getCheckOutDatetime());
        informationCustomer.setCreateAt(new Date());
        informationCustomer.setUpdateAt(new Date());
        informationCustomer.setStatus(Constant.COMMON_STATUS.ACTIVE);
//        List<Order> orderList = orderService.getList();
//        for (Order order : orderList) {
//            if (!order.getAccount().getCitizenId().equals(informationCustomerDTO.getCitizenId())) {
//                String customerCode = "KH" + (customerService.getList().size() + 1);
//                String ten = informationCustomerDTO.getFullname();
//                String tenThuong = ten.toLowerCase();
//                String randomTen = tenThuong.replaceAll("[^a-z0-9]", "");
//                Random rand = new Random();
//                int randomNum = rand.nextInt(900) + 100;
//                String username = randomTen + randomNum;
//                Customer customer = new Customer();
//                customer.setCustomerCode(customerCode);
//                customer.setUsername(username);
//                customer.setPassword("12345");
//                customer.setFullname(informationCustomerDTO.getFullname());
//                customer.setGender(informationCustomerDTO.getGender());
//                customer.setBirthday(informationCustomerDTO.getBirthday());
//                customer.setPhoneNumber(informationCustomerDTO.getPhoneNumber());
//                customer.setCitizenId(informationCustomerDTO.getCitizenId());
//                customer.setCreateAt(new Date());
//                customer.setUpdateAt(new Date());
//                customer.setStatus(1);
//                customerService.add(customer);
//            }
//            informationCustomerService.add(informationCustomer);
//        }
        informationCustomerService.add(informationCustomer);
        return new ResponseEntity<InformationCustomer>(informationCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        InformationCustomer informationCustomer = informationCustomerService.getById(id);
        informationCustomerService.delete(informationCustomer);
        return new ResponseEntity<String>("Xóa thành công", HttpStatus.OK);
    }

}
