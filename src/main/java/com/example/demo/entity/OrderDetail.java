package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "order_detail_code")
    private String orderDetailCode;

    @Column(name = "check_in_datetime")
    private Date checkInDatetime;

    @Column(name = "check_out_datetime")
    private Date checkOutDatetime;

    @Column(name = "time_in")
    private Integer timeIn;

    @Column(name = "room_price")
    private BigDecimal roomPrice;

    @Column(name = "customer_quantity")
    private Integer customerQuantity;

    @Column(name = "overdue_time")
    private Float overdueTime;

    @Column(name = "note")
    private String note;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_at")
    private Date updateAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted")
    private String deleted;

    @Column(name = "status")
    private Integer status;

    @JsonIgnore
    @OneToMany(mappedBy = "orderDetail", fetch = FetchType.LAZY)
    private List<ServiceUsed> serviceUsedList;

    @JsonIgnore
    @OneToMany(mappedBy = "orderDetail", fetch = FetchType.LAZY)
    private List<ComboUsed> comboUsedList;

    @JsonIgnore
    @OneToMany(mappedBy = "orderDetail", fetch = FetchType.LAZY)
    private List<InformationCustomer> informationCustomerList;

}
