package com.example.demo.dto;

import com.example.demo.constant.Constant;
import com.example.demo.entity.DiscountProgram;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class DiscountProgramDTO {

    private String id;
    private String name;
    private String code;
    private BigDecimal minimumInvoice;
    private BigDecimal maximumReductionValue;
    private Integer reduceValue;
    private Integer numberOfApplication;
    private Date startDay;
    private Date endDate;
    private Date createAt;
    private String createBy;
    private Date updateAt;
    private String updatedBy;
    private String deleted;
    private Integer status;
    private String textStatus;

}
