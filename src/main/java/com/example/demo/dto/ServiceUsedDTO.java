package com.example.demo.dto;

import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ServiceUsedDTO {

    private String id;
    private Service service;
    private String serviceId;
    private OrderDetail orderDetail;
    private String orderDetailId;
    private Integer quantity;
    private String note;
    private Date createAt;
    private String createBy;
    private Date updateAt;
    private String updatedBy;
    private String deleted;
    private Integer status;

}
