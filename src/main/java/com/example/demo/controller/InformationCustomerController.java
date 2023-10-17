package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.entity.Customer;
import com.example.demo.entity.InformationCustomer;
import com.example.demo.entity.OrderDetail;
import com.example.demo.service.CustomerService;
import com.example.demo.service.InformationCustomerService;
import com.example.demo.service.OrderDetailService;
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
    private CustomerService customerService;

    @GetMapping("/load")
    public List<InformationCustomer> load() {
        return informationCustomerService.getAll();
    }

    @GetMapping("/load/{id}")
    public List<InformationCustomer> loadByOrderId(@PathVariable("id") String id) {
        return informationCustomerService.findAllByOrderDetailId(id);
    }

    @PostMapping("/save/{id}")
    public ResponseEntity<InformationCustomer> save(@PathVariable("id") String id,
                                                    @RequestBody InformationCustomer informationCustomer) {
        Customer existingCustomer = customerService.findByCitizenId(informationCustomer.getCitizenId());
        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
        informationCustomer.setOrderDetail(orderDetail);
        informationCustomer.setStayFrom(orderDetail.getCheckInDatetime());
        informationCustomer.setStayTo(orderDetail.getCheckOutDatetime());
        informationCustomer.setCreateAt(new Date());
        informationCustomer.setUpdateAt(new Date());
        informationCustomer.setStatus(Constant.COMMON_STATUS.ACTIVE);

        if (existingCustomer == null) {
            Customer customer = new Customer();
            String customerCode = "KH" + (customerService.getCustomer().size());
            String ten = informationCustomer.getFullname();
            String tenThuong = ten.toLowerCase();
            String randomTen = tenThuong.replaceAll("[^a-z0-9]", "");
            Random rand = new Random();
            int randomNum = rand.nextInt(900) + 100;
            String username = randomTen + randomNum;
            customer.setCustomerCode(customerCode);
            customer.setUsername(username);
            customer.setPassword("123");
            customer.setFullname(informationCustomer.getFullname());
            customer.setGender(informationCustomer.getGender());
            customer.setBirthday(informationCustomer.getBirthday());
            customer.setPhoneNumber(informationCustomer.getPhoneNumber());
            customer.setCitizenId(informationCustomer.getCitizenId());
            customer.setEmail(informationCustomer.getEmail());
            customer.setAddress(informationCustomer.getAddress());
            customer.setNationality(informationCustomer.getNationality());
            customer.setCreateAt(new Date());
            customer.setUpdateAt(new Date());
            customer.setStatus(Constant.COMMON_STATUS.ACTIVE);
            customerService.add(customer);
        }

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
