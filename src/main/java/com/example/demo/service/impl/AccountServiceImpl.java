package com.example.demo.service.impl;

import com.example.demo.entity.Account;
import com.example.demo.entity.Position;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

//    @Override
//    public Boolean add(Account account) {
//        try {
//            if (accountRepository.findByEmail(account.getEmail()) != null || accountRepository.findByCitizenId(account.getCitizenId()) != null) {
//                return false;
//            }
//            account.setPassword(passwordEncoder.encode(account.getPassword()));
//            accountRepository.save(account);
//            return true;
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//    }

    @Override
    public Boolean add(Account account) {
        try {
            // Thêm các điều kiện kiểm tra null
            if (account == null || account.getEmail() == null || account.getCitizenId() == null || account.getPassword() == null) {
                return false;
            }

            synchronized (this) { // Sử dụng khóa để ngăn các request xảy ra đồng thời
                if (accountRepository.findByEmail(account.getEmail()) != null || accountRepository.findByCitizenId(account.getCitizenId()) != null) {
                    return false;
                }
                account.setPassword(passwordEncoder.encode(account.getPassword()));
                accountRepository.save(account);
            }
            return true;
        } catch (Exception e) {
            // Log lỗi thay vì chỉ in stack trace
            // Log dùng logger của bạn, ví dụ: logger.error("Error adding account", e);
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
    public Account getAccountByCode() {
        return accountRepository.getAccountByCode();
    }

}
