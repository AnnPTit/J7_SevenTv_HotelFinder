package com.example.demo.service;

import com.example.demo.config.UserInfoUserDetails;
import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserInfoDetailService implements UserDetailsService {
    private final AccountRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> userInfo = repository.findByEmail(email);
        return userInfo.map(UserInfoUserDetails::new)        
                .orElseThrow(() -> new UsernameNotFoundException("user not found: " + email));

    }

    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        Optional<Account> userInfo = repository.findById(id);
        return userInfo.map(UserInfoUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found: " + id));

    }
}
