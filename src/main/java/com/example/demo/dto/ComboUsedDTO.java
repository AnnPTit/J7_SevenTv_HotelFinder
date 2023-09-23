package com.example.demo.dto;

import com.example.demo.entity.Combo;
import com.example.demo.entity.OrderDetail;
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
public class ComboUsedDTO {

    private String id;
    private Combo combo;
    private OrderDetail orderDetail;
    private Integer quantity;
    private String note;
    private Date createAt;
    private String createBy;
    private Date updateAt;
    private String updatedBy;
    private String deleted;
    private Integer status;

}
