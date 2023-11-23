package com.example.demo.util;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BaseService {
    @Autowired
    private AccountRepository accountRepository;


    public Account getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication.getPrincipal() instanceof String)) {
            Optional<String> userNameLogin = SecurityUtils.getCurrentUserLogin();
            if (userNameLogin.isPresent()) {
                List<Account> users = accountRepository.findByEmailLs(userNameLogin.get());
                return users.get(0);
            }
        }
        return new Account();
    }


}
