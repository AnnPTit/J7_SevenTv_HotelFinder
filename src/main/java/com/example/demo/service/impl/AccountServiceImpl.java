package com.example.demo.service.impl;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<Account> getAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.getAll();
    }

    @Override
    public Page<Account> loadAndSearch(String accountCode, String fullname, String positionId, Pageable pageable) {
        return accountRepository.loadAndSearch(
                (accountCode != null && !accountCode.isEmpty()) ? accountCode : null,
                (fullname != null && !fullname.isEmpty()) ? "%" + fullname + "%" : null,
                (positionId != null && !positionId.isEmpty()) ? positionId : null,
                pageable
        );
    }


    @Override
    public Account findById(String id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean add(Account account) {
        try {
            if (accountRepository.findByEmail(account.getEmail()) != null || accountRepository.findByCitizenId(account.getCitizenId()) != null){
                return false;
            }
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            accountRepository.save(account);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    @Override
//    public Account add(Account account) {
//        if (accountRepository.existsByEmail(account.getEmail())) {
//            throw new RuntimeException("Email đã tồn tại trong hệ thống");
//        }
//        if (accountRepository.existsByCitizenId(account.getCitizenId())) {
//            throw new RuntimeException("Căn cước công dân đã tồn tại trong hệ thống");
//        }
//        account.setPassword(passwordEncoder.encode(account.getPassword()));
//        return accountRepository.save(account);
//    }


    @Override
    public Boolean delete(String id) {
        try {
            accountRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Account findByCitizenId(String citizenId) {
        return accountRepository.findByCitizenId(citizenId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCitizenId(String citizenId) {
        return accountRepository.existsByCitizenId(citizenId);
    }


}
