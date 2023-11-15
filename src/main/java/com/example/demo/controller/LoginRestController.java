package com.example.demo.controller;

import com.example.demo.config.UserInfoUserDetails;
import com.example.demo.constant.Constant;
import com.example.demo.entity.Account;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.payload.LoginRequest;
import com.example.demo.payload.LoginResponse;
import com.example.demo.payload.RandomStuff;
import com.example.demo.repository.AccountRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class LoginRestController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public LoginResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // Xác thực từ username và password.
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()

                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        Account account = accountRepository.findByEmailAndStatus(loginRequest.getUsername(), Constant.COMMON_STATUS.ACTIVE);
        if (account == null || account.getStatus() == Constant.COMMON_STATUS.UNACTIVE) {
            return null;
        }
        System.out.println("Hello");
        System.out.println(authentication.getPrincipal());

        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((UserInfoUserDetails) authentication.getPrincipal());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(jwt);
        loginResponse.setIdUser(((UserInfoUserDetails) authentication.getPrincipal()).getId());
        loginResponse.setFullName(((UserInfoUserDetails) authentication.getPrincipal()).getFullName());
        System.out.println("idUser: " + ((UserInfoUserDetails) authentication.getPrincipal()).getId());
        System.out.println("name: " + ((UserInfoUserDetails) authentication.getPrincipal()).getFullName());
        return loginResponse;
    }

    // Api /api/random yêu cầu phải xác thực mới có thể request
    @GetMapping("/random")
    public RandomStuff randomStuff() {
        return new RandomStuff("JWT Hợp lệ mới có thể thấy được message này");
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "Bạn Không có thẩm quyền truy cập vào trang này ";
    }

}
