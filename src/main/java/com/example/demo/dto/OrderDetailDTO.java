package com.example.demo.dto;

import com.example.demo.entity.ComboUsed;
import com.example.demo.entity.InformationCustomer;
import com.example.demo.entity.Order;
import com.example.demo.entity.Room;
import com.example.demo.entity.ServiceUsed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDetailDTO {

    private String id;
    private Order order;
    private String orderId;
    private Room room;
    private String roomId;
    private String orderDetailCode;
    private Date checkIn;
    private Date checkOut;
    private Date checkOutReal;
    private Integer timeIn;
    private BigDecimal roomPrice;
    private Integer customerQuantity;
    private Integer overdueTime;
    private String note;
    private Date createAt;
    private String createBy;
    private Date updateAt;
    private String updatedBy;
    private String deleted;
    private Integer status;

    private List<String> roomImages;
    private List<ServiceUsed> serviceUsedList;
    private List<InformationCustomer> informationCustomerList;
    private List<ComboUsed> comboUsedList;

}
