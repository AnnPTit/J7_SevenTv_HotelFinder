package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.model.Mail;
import com.example.demo.service.AccountService;
import com.example.demo.service.MailService;
import com.example.demo.service.PositionService;
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
@RequestMapping("/api/admin/account")
public class AccountController {


    @Autowired
    private AccountService accountService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private MailService mailService;

    @GetMapping("/load")
    public Page<Account> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return accountService.getAll(pageable);
    }

    @GetMapping("getAll")
    public List<Account> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/loadAndSearch")
    public Page<Account> findByCodeOrName(@RequestParam(name = "key", defaultValue = "") String key,
                                          @RequestParam(name = "positionId", defaultValue = "") String positionId,
                                          @RequestParam(name = "current_page", defaultValue = "0") int current_page
    ) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return accountService.loadAndSearch(key, key, positionId, pageable);
    }

    @PostMapping("/save")
    public ResponseEntity<Account> add(@Valid @RequestBody Account account,
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
        account.setCreateAt(new Date());
        account.setUpdateAt(new Date());
        account.setStatus(1);
        account.setPassword("123");
        account.setPosition(positionService.getIdPosition());

        Mail mail = new Mail();
        mail.setMailFrom("phamthanhanzwz@gmail.com");
        mail.setMailTo(account.getEmail());
        mail.setMailSubject("Thông tin tài khoản website");
        mail.setMailContent(
                        "Dear: " + account.getFullname() + "\n" +
                        "Email của bạn là: " + account.getEmail() + "\n" + "\n" +
                        "password: " + account.getPassword() + "\n"+
                        "Đây là email tự động xin vui lòng không trả lời <3");
        accountService.add(account);
        mailService.sendEmail(mail);
        return new ResponseEntity<Account>(account, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Account> delete(@PathVariable("id") String id) {
        Account account = accountService.findById(id);
        account.setStatus(0);
        accountService.add(account);
        return new ResponseEntity("Deleted", HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Account> detail(@PathVariable("id") String id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Account> update(@PathVariable("id") String id,
                                          @RequestBody Account account,
                                          BindingResult result) {
//        Account account1 = accountService.findById(id);
//        if (result.hasErrors()) {
//            Map<String, String> errorMap = new HashMap<>();
//            for (FieldError error : result.getFieldErrors()) {
//                String key = error.getField();
//                String value = error.getDefaultMessage();
//                errorMap.put(key, value);
//            }
//            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
//        }
        account.setId(id);
        account.setUpdateAt(new Date());
        accountService.add(account);
        return new ResponseEntity<Account>(account, HttpStatus.OK);
    }

//    @PostMapping("/mail")
//    public ResponseEntity<Mail> add() {
//
//        return new ResponseEntity<Mail>(mail, HttpStatus.OK);
//    }
}
