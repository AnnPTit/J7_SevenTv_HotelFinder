package com.example.demo.service;

import com.example.demo.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {

    Page<Account> getAll(Pageable pageable);

    List<Account> findAll();

    Page<Account> findByCodeOrName(String key , Pageable pageable);

    Account findById(String id);

    Account add(Account account);

    Boolean delete(String id);

}
