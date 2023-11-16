package com.example.demo.util;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

public abstract class BaseService {

    private static AccountRepository accountRepository;


    public static Account getCurrentUser() {
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

    public static void setAccountRepository(AccountRepository accountRepository) {
        BaseService.accountRepository = accountRepository;
    }
}
