package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.CustomerLoginDTO;
import com.example.demo.entity.Customer;
import com.example.demo.model.Mail;
import com.example.demo.service.CustomerService;
import com.example.demo.service.MailService;
import com.example.demo.util.BaseService;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    private MailService mailService;

    @Autowired
    private BaseService baseService;

    @GetMapping("/load")
    public Page<Customer> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return customerService.getAll(pageable);
    }

    @GetMapping("/getAll")
    public List<Customer> findAllWithSearch(@RequestParam(name = "key", defaultValue = "") String key) {
        return customerService.findAllByStatus(key, key, key);
    }

    @GetMapping("/getAllByOrderId/{id}")
    public List<Customer> findAll(@PathVariable("id") String id) {
        return customerService.getAllCustomer(id);
    }

    @GetMapping("/countByStatus")
    public Long countByStatus() {
        return customerService.countCustomerByStatus();
    }

    @GetMapping("/getAllByOrderDetailId/{id}")
    public List<Customer> findAllByOrderDetail(@PathVariable("id") String id) {
        return customerService.getAllCustomerByOrderDetailId(id);
    }

    @GetMapping("/loadAndSearch")
    public Page<Customer> findByCodeOrName(@RequestParam(name = "key", defaultValue = "") String key,
                                           @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return customerService.loadAndSearch(key, key, key, pageable);
    }

    @PostMapping("/save")
    public ResponseEntity<Customer> add(@Valid @RequestBody Customer customer,
                                        BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }

        if (customer.getUsername().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Username", HttpStatus.BAD_REQUEST);
        }

        if (customer.getFullname().isBlank()) {
            return new ResponseEntity("Full name không được bỏ trống", HttpStatus.BAD_REQUEST);
        }

        if (customer.getBirthday() == null) {
            return new ResponseEntity("Ngày sinh không được để trống!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getEmail().isBlank()) {
            return new ResponseEntity("Email không được để trống!", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$")) {
            return new ResponseEntity("Email không đúng định dạng!", HttpStatus.BAD_REQUEST);
        }
        if (customerService.existsByEmail(customer.getEmail())) {
            return new ResponseEntity("Email đã tồn tại, Vui lòng nhập E-mail mới!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getPhoneNumber().isBlank()) {
            return new ResponseEntity("Số điện thoại không được bỏ trống", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getPhoneNumber().matches("^(\\+84|0)[35789][0-9]{8}$")) {
            return new ResponseEntity("Số điện thoại không đúng định dạng!!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getCitizenId().isBlank()) {
            return new ResponseEntity("Căn cước công dân không được để trống!!", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getCitizenId().matches("\\d{12}")) {
            return new ResponseEntity("Căn cước công dân không đúng định dạng!!", HttpStatus.BAD_REQUEST);
        }
        if (customerService.existsByCitizenId(customer.getCitizenId())) {
            return new ResponseEntity("Căn cước công dân đã tồn tại!!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getPassword().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Password", HttpStatus.BAD_REQUEST);
        }
        if (customer.getPassword().length() < 5 || customer.getPassword().length() > 20) {
            return new ResponseEntity("Password phải từ 5-20 kí tự.", HttpStatus.BAD_REQUEST);
        }

        if (customer.getProvinces().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Tỉnh.", HttpStatus.BAD_REQUEST);
        }
        if (customer.getDistricts().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Huyện.", HttpStatus.BAD_REQUEST);
        }
        if (customer.getWards().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Xã.", HttpStatus.BAD_REQUEST);
        }

        customer.setCustomerCode(customerService.generateCustomerCode());
        customer.setStatus(Constant.COMMON_STATUS.ACTIVE);
        customer.setCreateAt(new Date());
        customer.setUpdateAt(new Date());
        customer.setCreateBy(baseService.getCurrentUser().getFullname());
        customer.setUpdatedBy(baseService.getCurrentUser().getFullname());

        Mail mail = new Mail();
        mail.setMailFrom("nguyenvantundz2003@gmail.com");
        mail.setMailTo(customer.getEmail());
        mail.setMailSubject("Thông tin tài khoản website");
        mail.setMailContent(
                "Dear: " + customer.getFullname() + "\n" +
                        "Email của bạn là: " + customer.getEmail() + "\n" +
                        "password: " + customer.getPassword() + "\n" + "\n" +
                        "Đây là email tự động xin vui lòng không trả lời <3");
        customerService.add(customer);
        mailService.sendEmail(mail);
        customerService.add(customer);
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Customer> delete(@PathVariable("id") String id) {
        Customer customer = customerService.findById(id);
        customer.setStatus(Constant.COMMON_STATUS.UNACTIVE);
        customerService.add(customer);
        return new ResponseEntity("Deleted", HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Customer> detail(@PathVariable("id") String id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> update(
            @PathVariable("id") String id,
            @Valid @RequestBody Customer customer,
            BindingResult result) {
        customer.setId(id);
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }

        if (customer.getUsername().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Username", HttpStatus.BAD_REQUEST);
        }

        if (customer.getFullname().isBlank()) {
            return new ResponseEntity("Full name không được bỏ trống", HttpStatus.BAD_REQUEST);
        }

        if (customer.getBirthday() == null) {
            return new ResponseEntity("Ngày sinh không được để trống!", HttpStatus.BAD_REQUEST);
        }
        // Validate birthday against a regex pattern
//        if (!customer.getBirthday().toString().matches("\\d{4}-\\d{2}-\\d{2}")) {
//            return new ResponseEntity("Ngày sinh không đúng định dạng (yyyy-MM-dd)!!", HttpStatus.BAD_REQUEST);
//        }
        // Validate age against the minimum required age
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.YEAR, -18);
//        if (customer.getBirthday().after(calendar.getTime())) {
//            return new ResponseEntity("Bạn phải ít nhất 18 tuổi!!", HttpStatus.BAD_REQUEST);
//        }

        if (customer.getEmail().isBlank()) {
            return new ResponseEntity("Email không được bỏ trống", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$")) {
            return new ResponseEntity("Email không đúng định dạng!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getPhoneNumber().isBlank()) {
            return new ResponseEntity("Số điện thoại không được bỏ trống", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getPhoneNumber().matches("^(\\+84|0)[35789][0-9]{8}$")) {
            return new ResponseEntity("Số điện thoại không đúng định dạng!!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getCitizenId().isBlank()) {
            return new ResponseEntity("Căn cước công dân không được để trống!!", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getCitizenId().matches("\\d{12}")) {
            return new ResponseEntity("Căn cước công dân không đúng định dạng!!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getPassword().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Password", HttpStatus.BAD_REQUEST);
        }
        if (customer.getPassword().length() < 5 || customer.getPassword().length() > 20) {
            return new ResponseEntity("Password phải từ 5-20 kí tự.", HttpStatus.BAD_REQUEST);
        }

        if (customer.getProvinces().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Tỉnh.", HttpStatus.BAD_REQUEST);
        }
        if (customer.getDistricts().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Huyện.", HttpStatus.BAD_REQUEST);
        }
        if (customer.getWards().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Xã.", HttpStatus.BAD_REQUEST);
        }

        customer.setUpdateAt(new Date());
        customer.setUpdatedBy(baseService.getCurrentUser().getFullname());
        customerService.add(customer);
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

}
