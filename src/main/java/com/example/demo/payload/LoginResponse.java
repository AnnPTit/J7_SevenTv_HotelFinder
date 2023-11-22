package com.example.demo.payload;

import com.example.demo.entity.Position;
import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String idUser;
    private String fullName;
    private String position;

//    public LoginResponse(String accessToken) {
//        this.accessToken = accessToken;
//    }
}
