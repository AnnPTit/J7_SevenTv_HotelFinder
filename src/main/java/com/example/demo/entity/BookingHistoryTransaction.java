package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "history_transaction _booking")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingHistoryTransaction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "id_booking")
    private String idBooking;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "payment_method")
    private Integer paymentMethod ;

    @Column(name = "type")
    private Integer type ;

    @Column(name = "note")
    private String note;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "cancel_date")
    private Date cancelDate;

    @Column(name = "refund_money")
    private BigDecimal refundMoney;

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
}
