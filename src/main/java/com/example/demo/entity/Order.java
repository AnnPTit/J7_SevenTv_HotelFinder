package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
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
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@Entity
@Table(name = "order")
public class Order {
    @Id
    @Column(name = "id")
    @GenericGenerator(name = "ganerator", strategy = "uuid2", parameters = {})
    @GeneratedValue(generator = "ganerator")
    private UUID id;

    @Column(name = "book_room_id")
    private UUID bookRoomId;

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
    private UUID createBy;

    @Column(name = "update_at")
    private Date updateAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted")
    private UUID deleted;

    @Column(name = "status")
    private Integer status;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<HistoryTransaction> historyTransactionList;
    @OneToMany(mappedBy = "orderPayMent", fetch = FetchType.LAZY)
    private List<PaymentMethod> paymentMethodList;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetailList;

}
