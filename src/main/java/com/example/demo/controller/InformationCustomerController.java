package com.example.demo.controller;

import com.example.demo.dto.InformationCustomerDTO;
import com.example.demo.entity.InformationCustomer;
import com.example.demo.entity.OrderDetail;
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

@RestController
@CrossOrigin("*")
@RequestMapping("/api/information-customer")
public class InformationCustomerController {

    @Autowired
    private InformationCustomerService informationCustomerService;
    @Autowired
    private OrderDetailService orderDetailService;

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
        informationCustomer.setStatus(1);
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
