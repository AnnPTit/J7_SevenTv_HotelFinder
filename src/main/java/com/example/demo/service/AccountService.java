package com.example.demo.service;

import com.example.demo.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountService {

    Page<Account> getAll(Pageable pageable);

    List<Account> findAll();

    Page<Account> loadAndSearch(String accountCode, String fullname, String positionId, Pageable pageable);

    Account findById(String id);

    Boolean add(Account account);

    Boolean delete(String id);

    Account getAccountByCode();

}
