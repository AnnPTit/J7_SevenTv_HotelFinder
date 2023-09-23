package com.example.demo.controller;

import com.example.demo.dto.ServiceUsedDTO;
import com.example.demo.entity.InformationCustomer;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Service;
import com.example.demo.entity.ServiceUsed;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.ServiceService;
import com.example.demo.service.ServiceUsedSerivce;
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
@RequestMapping("/api/service-used")
public class ServiceUsedController {

    @Autowired
    private ServiceUsedSerivce serviceUsedSerivce;

    @GetMapping("/load")
    public List<ServiceUsed> load() {
        return serviceUsedSerivce.getAll();
    }

    @GetMapping("/load/{id}")
    public List<ServiceUsed> loadByOrderDetailId(@PathVariable("id") String id) {
        return serviceUsedSerivce.getAllByOrderDetailId(id);
    }

    @PostMapping("/save")
    public ResponseEntity<ServiceUsed> add(@RequestBody ServiceUsedDTO serviceUsedDTO) {
        ServiceUsed serviceUsed = new ServiceUsed();
        serviceUsed.setService(serviceUsedDTO.getService());
        serviceUsed.setOrderDetail(serviceUsedDTO.getOrderDetail());
        serviceUsed.setQuantity(serviceUsedDTO.getQuantity());
        serviceUsed.setNote(serviceUsedDTO.getNote());
        serviceUsed.setCreateAt(new Date());
        serviceUsed.setUpdateAt(new Date());
        serviceUsed.setStatus(1);
        serviceUsedSerivce.add(serviceUsed);
        return new ResponseEntity<ServiceUsed>(serviceUsed, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        ServiceUsed serviceUsed = serviceUsedSerivce.getById(id);
        serviceUsedSerivce.delete(serviceUsed);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

}
