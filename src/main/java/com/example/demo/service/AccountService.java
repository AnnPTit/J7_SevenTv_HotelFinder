package com.example.demo.service;

import com.example.demo.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    Page<Account> getAll(Pageable pageable);

    List<Account> findAll();

    Page<Account> loadAndSearch(String accountCode, String fullname, String positionId, Pageable pageable);

    Account findById(String id);

    Boolean add(Account account);

    Boolean update(Account account);

    Boolean delete(String id);

    Optional<Account> findByEmail(String email);

    Account getAccountByCode();

    String generateAccountCode();

    boolean existsByEmail(String email);

    boolean existsByCitizenId(String citizenId);

}
