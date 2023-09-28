package com.example.demo.controller;

import com.example.demo.dto.ComboListDTO;
import com.example.demo.entity.Combo;
import com.example.demo.entity.Service;
import com.example.demo.service.ComboService;
import com.example.demo.service.ComboServiceService;
import com.example.demo.service.ServiceService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/combo")
public class ComboController {
    @Autowired
    private ComboService comboService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private ComboServiceService comboServiceService;

    @GetMapping("/load")
    public Page<Combo> load(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return comboService.getAll(pageable);
    }

    @GetMapping("/search")
    public ComboListDTO search(@RequestParam(name = "current_page", defaultValue = "0") int current_page,
                               @RequestParam(name = "key", defaultValue = "") String key,
                               @RequestParam(name = "serviceId", defaultValue = "") String serviceId,
                               @RequestParam(name = "start", defaultValue = "0") BigDecimal start,
                               @RequestParam(name = "end", defaultValue = "10000000000000000000") BigDecimal end) {

        int pageSize = 5;
        int offset = pageSize * current_page;

        long totalRecords = comboService.countSearch(key, key, serviceId, start, end);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        List<Combo> list = comboService.searchCombosWithService(key, key, serviceId, start, end, pageSize, offset);

        ComboListDTO comboListDTO = new ComboListDTO();
        comboListDTO.setContent(list);
        comboListDTO.setTotalPages(totalPages);
        comboListDTO.setTotalElements(totalRecords);
        return comboListDTO;
    }

    @GetMapping("/getAll")
    public List<Combo> getAll() {
        return comboService.getAll();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Combo> detail(@PathVariable("id") String id) {
        Combo combo = comboService.findById(id);
        return new ResponseEntity<Combo>(combo, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody Map<String, Object> payload, BindingResult result) {
        // Kiểm tra lỗi trong payload
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra các trường có giá trị null hoặc rỗng
        List<String> emptyFields = new ArrayList<>();
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Kiểm tra các trường có giá trị null hoặc rỗng
            if (value == null || value.toString().trim().isEmpty()) {
                emptyFields.add(key);
            } else if (value instanceof Number) {
                // Kiểm tra trường kiểu dữ liệu số
                if (isInvalidNumberValue((Number) value)) {
                    // Xử lý khi giá trị số không hợp lệ
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", "Số không hợp lệ !");
                    response.put("field", key);
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } else if (value instanceof List) {
                // Kiểm tra trường kiểu dữ liệu mảng
                if (isInvalidListValue((List<?>) value)) {
                    // Xử lý khi giá trị mảng không hợp lệ
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", "Danh sách dịch vụ không được để trống !");
                    response.put("field", key);
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
        }
        // Nếu có trường trống, trả về lỗi BadRequest và thông báo về các trường trống
        if (!emptyFields.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Không được để trống !");
            response.put("fields", emptyFields);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Tiếp tục xử lý dữ liệu trong payload và tạo Combo nếu cần
        System.out.println("Received payload data:");
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + ": " + value);
        }
        Combo combo = createComboFromPayload(payload);
        if (combo == null) {
            return new ResponseEntity<>("Tham số truyền vào không hợp lệ !", HttpStatus.BAD_REQUEST);
        }

        if (comboService.existsByCode(combo.getComboCode())) {
            return new ResponseEntity<>("Mã combo đã tồn tại !", HttpStatus.BAD_REQUEST);
        }
        // Them Combo vao cosoDulieu
        combo.setCreateAt(new Date());
        combo.setStatus(1);
        comboService.add(combo);
        // Sau khi add combo -> add combo_service
        // Lấy giá trị của key "service" và kiểm tra xem nó có là mảng không
        Object serviceValue = payload.get("service");
        if (serviceValue instanceof List) {
            List<?> serviceList = (List<?>) serviceValue;
            // Xử lý các phần tử trong mảng serviceList
            for (Object serviceItem : serviceList) {
                if (serviceItem instanceof String) {
                    // Xử lý mỗi phần tử của mảng kiểu String
                    String serviceString = (String) serviceItem;
                    // Thực hiện xử lý với serviceString
                    Service service = serviceService.findById(serviceString);
                    com.example.demo.entity.ComboService comboService1 = new com.example.demo.entity.ComboService();
                    comboService1.setCombo(combo);
                    comboService1.setService(service);
                    comboService1.setCreateAt(new Date());
                    comboService1.setPrice(service.getPrice());
                    comboService1.setStatus(1);
                    comboServiceService.add(comboService1);
                    System.out.println("Add Service: " + serviceString);
                } else {
                    // Xử lý khi phần tử không phải kiểu String (nếu có yêu cầu)
                }
            }
        } else {
            // Xử lý khi giá trị của key "service" không phải là mảng (nếu có yêu cầu)
        }


        return new ResponseEntity<>("Data received and validated successfully", HttpStatus.OK);
    }

    // Hàm kiểm tra giá trị số có hợp lệ hay không (ví dụ)
    private boolean isInvalidNumberValue(Number number) {
        // Điều kiện kiểm tra số không hợp lệ
        // Ví dụ: Giá trị số phải lớn hơn 0
        return number.doubleValue() <= 0;
    }

    // Hàm kiểm tra giá trị mảng có hợp lệ hay không (ví dụ)
    private boolean isInvalidListValue(List<?> list) {
        // Điều kiện kiểm tra mảng không hợp lệ
        // Ví dụ: Mảng không được rỗng
        return list.isEmpty();
    }


    private Combo createComboFromPayload(Map<String, Object> payload) {
        String comboCode = (String) payload.get("comboCode");
        String comboName = (String) payload.get("comboName");
        String note = (String) payload.get("note");
        Object priceObj = payload.get("price");
        BigDecimal price;

        if (priceObj instanceof BigDecimal) {
            price = (BigDecimal) priceObj;
        } else if (priceObj instanceof String) {
            try {
                price = new BigDecimal((String) priceObj);
            } catch (NumberFormatException e) {
                // Xử lý khi giá trị price không hợp lệ
                return null;
            }
        } else {
            // Xử lý khi kiểu dữ liệu không hợp lệ cho trường price
            return null;
        }

        // Kiểm tra nếu có trường nào bị trống thì không tạo Combo
        if (comboCode == null || comboName == null || note == null) {
            return null;
        }

        // Tạo đối tượng Combo từ các giá trị đã xử lý
        Combo combo = new Combo();
        combo.setComboCode(comboCode);
        combo.setComboName(comboName);
        combo.setNote(note);
        combo.setPrice(price);
        return combo;
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Combo> update(@RequestBody Combo combo) {
        comboService.add(combo);
        return new ResponseEntity<>(combo, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Combo> detele(@PathVariable("id") String id) {
        Combo combo = comboService.findById(id);
        combo.setStatus(0);
        List<com.example.demo.entity.ComboService> list = combo.getComboServiceList();
        for (com.example.demo.entity.ComboService comboService : list
        ) {
            comboService.setStatus(0);
            comboServiceService.add(comboService);
        }
        comboService.add(combo);
        return new ResponseEntity("Deleted", HttpStatus.OK);
    }

}
