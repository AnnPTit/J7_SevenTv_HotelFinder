package com.example.demo.service.impl;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            accountRepository.save(account);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void updatePasse(String pass, String id) {
        accountRepository.updatePass(passwordEncoder.encode(pass), id);
    }

    @Override
    public Boolean update(Account account) {
        try {
            accountRepository.save(account);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

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

    public Account getAccountByCode() {
        return accountRepository.getAccountByCode();
    }

    public String generateAccountCode() {
        int CODE_LENGTH = 8;
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            code.append(randomChar);
        }

        return code.toString();
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
