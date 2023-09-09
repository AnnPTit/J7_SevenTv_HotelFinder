package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VNPayParam {

    private String vnp_OrderInfo;

    private String orderType;

    private Integer amount;

    private String language;

    private String backCode;

}
