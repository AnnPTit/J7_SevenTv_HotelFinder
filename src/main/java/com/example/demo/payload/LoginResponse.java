package com.example.demo.payload;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String idUser;
    private String fullName;
    private String position;
}
