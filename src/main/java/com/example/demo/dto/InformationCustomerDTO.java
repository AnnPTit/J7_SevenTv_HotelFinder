package com.example.demo.dto;

import com.example.demo.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InformationCustomerDTO {

    private String id;
    private OrderDetail orderDetail;
    private String fullname;
    private Boolean gender;
    private Date birthday;
    private String phoneNumber;
    private String citizenId;
    private String passport;
    private String nationality;
    private Date stayFrom;
    private Date stayTo;
    private Date createAt;
    private String createBy;
    private Date updateAt;
    private String updatedBy;
    private String deleted;
    private Integer status;

}
