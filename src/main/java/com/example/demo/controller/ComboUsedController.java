package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.ComboUsedDTO;
import com.example.demo.entity.Combo;
import com.example.demo.entity.ComboUsed;
import com.example.demo.entity.OrderDetail;
import com.example.demo.service.ComboService;
import com.example.demo.service.ComboUsedService;
import com.example.demo.service.OrderDetailService;
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

import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/combo-used")
public class ComboUsedController {

    @Autowired
    private ComboUsedService comboUsedService;
    @Autowired
    private ComboService comboService;
    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/load")
    public List<ComboUsed> getAll() {
        return comboUsedService.getAll();
    }

    @GetMapping("/load/{id}")
    public List<ComboUsed> loadByOrderDetailId(@PathVariable("id") String id) {
        return comboUsedService.getAllByOrderDetailId(id);
    }

    @PostMapping("/save")
    public ResponseEntity<ComboUsed> add(@RequestBody ComboUsedDTO comboUsedDTO) {
        Combo combo = comboService.findById(comboUsedDTO.getCombo());
        OrderDetail orderDetail = orderDetailService.getOrderDetailById(comboUsedDTO.getOrderDetail());
        ComboUsed existingComboUsed = comboUsedService.getByCombo(comboUsedDTO.getCombo(), comboUsedDTO.getOrderDetail());

        if (existingComboUsed != null) {
            existingComboUsed.setQuantity(existingComboUsed.getQuantity() + comboUsedDTO.getQuantity());
            comboUsedService.updateQuantityComboUsed(existingComboUsed.getQuantity(), existingComboUsed.getCombo().getId());
            return new ResponseEntity<ComboUsed>(existingComboUsed, HttpStatus.OK);
        } else {
            ComboUsed comboUsed = new ComboUsed();
            comboUsed.setCombo(combo);
            comboUsed.setOrderDetail(orderDetail);
            comboUsed.setQuantity(comboUsedDTO.getQuantity());
            comboUsed.setNote(comboUsedDTO.getNote());
            comboUsed.setCreateAt(new Date());
            comboUsed.setUpdateAt(new Date());
            comboUsed.setStatus(Constant.COMMON_STATUS.ACTIVE);
            comboUsedService.add(comboUsed);
            return new ResponseEntity<ComboUsed>(comboUsed, HttpStatus.OK);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody ComboUsedDTO comboUsedDTO) {
        comboUsedService.updateQuantity(comboUsedDTO.getQuantity(), comboUsedDTO.getId());
        return new ResponseEntity<String>("Update thành công", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        ComboUsed comboUsed = comboUsedService.getById(id);
        comboUsedService.delete(comboUsed);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

}
