package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserObject {
    private String hoVaTen;
    private String email;
    private String soDienThoai;
}
