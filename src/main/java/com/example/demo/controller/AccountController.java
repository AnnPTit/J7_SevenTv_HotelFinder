package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/load")
    public List<Account> getAll() {
        return accountService.getAll();
    }

    @PostMapping("/add")
    public Account add(@RequestBody Account account) {
        account.setCreateAt(new Date());
        return accountService.add(account);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable("id") String id) {
        return accountService.delete(id);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Account> detail(@PathVariable("id") String id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Account> update(@PathVariable("id") String id, @RequestBody Account account) {
        account.setId(id);
        account.setUpdateAt(new Date());
        accountService.add(account);
        return ResponseEntity.ok(account);
    }
}
