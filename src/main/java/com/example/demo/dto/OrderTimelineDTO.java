package com.example.demo.dto;

import com.example.demo.entity.Account;
import com.example.demo.entity.Order;
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
public class OrderTimelineDTO {

    private String id;
    private Integer type;
    private String note;
    private Date createAt;
    private Order order;
    private Account account;

}
