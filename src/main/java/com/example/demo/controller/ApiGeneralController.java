package com.example.demo.controller;

import com.example.demo.dto.RevenueDTO;
import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import com.example.demo.service.OrderService;
import com.example.demo.util.ExcelExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/general")
public class ApiGeneralController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/getCustomerDifferenceOrder/{orderId}/{orderDetailId}")
    public List<Customer> getCustomerDifferentOrder(@PathVariable("orderId") String orderId,
                                                    @PathVariable("orderDetailId") String orderDetailId) {
        return customerService.getCustomerDifferentOrder(orderId, orderDetailId);
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
