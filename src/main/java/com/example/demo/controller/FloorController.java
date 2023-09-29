package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.entity.Floor;
import com.example.demo.entity.ServiceType;
import com.example.demo.service.FloorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/floor")
public class FloorController {

    @Autowired
    private FloorService floorService;

    @GetMapping("/getList")
    public List<Floor> getList() {
        return floorService.getList();
    }

    @GetMapping("/load")
    public Page<Floor> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return floorService.getAll(pageable);
    }

    @GetMapping("/search")
    public Page<Floor> findByCodeOrName(@RequestParam(name = "key") String key,
                                        @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        if (key == "") {
            return floorService.getAll(pageable);
        }

        return floorService.findByCodeOrName(key, pageable);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Floor> detail(@PathVariable("id") String id) {
        Floor floor = floorService.getFloorById(id);
        return new ResponseEntity<Floor>(floor, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Floor> save(@Valid @RequestBody Floor floor, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
        if (floor.getFloorCode().trim().isEmpty() || floor.getFloorName().trim().isEmpty()) {
            return new ResponseEntity("Không được để trống", HttpStatus.BAD_REQUEST);
        }

        if (floorService.existsByCode(floor.getFloorCode())) {
            return new ResponseEntity("Mã đã tồn tại!", HttpStatus.BAD_REQUEST);
        }
        if (floor.getNote().trim().isEmpty()) {
            floor.setNote("No note.");
        }
        floor.setCreateAt(new Date());
        floor.setUpdateAt(new Date());
        floor.setStatus(Constant.COMMON_STATUS.ACTIVE);
        floorService.add(floor);
        return new ResponseEntity<Floor>(floor, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Floor> update(@PathVariable("id") String id,
                                        @Valid @RequestBody Floor floor,
                                        BindingResult result) {
        Floor fl = floorService.getFloorById(id);
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
        if (floor.getFloorName().trim().isEmpty() || floor.getNote().trim().isEmpty()) {
            return new ResponseEntity("Không được để trống", HttpStatus.BAD_REQUEST);
        }

        fl.setFloorCode(floor.getFloorCode());
        fl.setFloorName(floor.getFloorName());
        fl.setNote(floor.getNote());
        fl.setUpdateAt(new Date());
        floorService.add(fl);
        return new ResponseEntity<Floor>(fl, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Floor> delete(@PathVariable("id") String id) {
        Floor floor = floorService.getFloorById(id);
        floor.setStatus(Constant.COMMON_STATUS.UNACTIVE);
        floorService.add(floor);
        return new ResponseEntity("Deleted", HttpStatus.OK);
    }

}
