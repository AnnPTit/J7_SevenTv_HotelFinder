package com.example.demo.entity;

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
@Table(name = "`order`")
public class Order {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

//    @ManyToOne
//    @JoinColumn(name = "book_room_id", nullable = false)
//    private BookRoom bookRoom;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "type_of_order")
    private Boolean typeOfOrder;

    @Column(name = "money_given_by_customer")
    private BigDecimal moneyGivenByCustomer;

    @Column(name = "excess_money")
    private BigDecimal excessMoney;

    @Column(name = "deposit")
    private BigDecimal deposit;

    @Column(name = "booking_date_start")
    private Date bookingDateStart;

    @Column(name = "booking_date_end")
    private Date bookingDateEnd;

    @Column(name = "surcharge")
    private BigDecimal surcharge;

    @Column(name = "vat")
    private BigDecimal vat;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "total_money")
    private BigDecimal totalMoney;

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
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<HistoryTransaction> historyTransactionList;

    @JsonIgnore
    @OneToMany(mappedBy = "orderPayment", fetch = FetchType.LAZY)
    private List<PaymentMethod> paymentMethodList;

    @JsonIgnore
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetailList;

    @JsonIgnore
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderTimeline> orderTimelineList;

}
