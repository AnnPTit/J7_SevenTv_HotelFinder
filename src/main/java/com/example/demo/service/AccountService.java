package com.example.demo.service;

import com.example.demo.entity.Account;

import java.util.List;

public interface AccountService {

    List<Account> getAll();

    Account getAccountById(String id);

    Account add(Account account);

    Boolean delete(String id);

}
