package com.example.demo.controller;

import com.example.demo.entity.Deposit;
import com.example.demo.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/deposit")
public class DepositController {

    @Autowired
    private DepositService depositService;

    @GetMapping("/load")
    public List<Deposit> getAll() {
        return depositService.getAll();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Deposit> detail(@PathVariable("id") String id) {
        Deposit deposit = depositService.getDepositById(id);
        System.out.println("Id: " + id);
        System.out.println("Deposit: " + deposit.toString());
        return new ResponseEntity<Deposit>(deposit, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        depositService.delete(id);
        return new ResponseEntity<String>("Deleted id '" + id + "' successful", HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Deposit> add(@RequestBody Deposit deposit) {
        depositService.add(deposit);
        return new ResponseEntity<Deposit>(deposit, HttpStatus.OK);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Deposit> update(@PathVariable("id") String id, @RequestBody Deposit deposit) {
        Deposit dps = depositService.getDepositById(id);
        dps.setPileValue(deposit.getPileValue());
        dps.setUnit(deposit.getUnit());
        dps.setStatus(deposit.getStatus());
        depositService.add(dps);
        return new ResponseEntity<Deposit>(dps, HttpStatus.OK);
    }
}
