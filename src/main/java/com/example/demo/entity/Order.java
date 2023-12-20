package com.example.demo.entity;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.OrderDetailExport;
import com.example.demo.dto.OrderExportDTO;
import com.example.demo.dto.RoomResponeDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
@SqlResultSetMappings(
        value = {
                @SqlResultSetMapping(
                        name = "orderResult",
                        classes = {
                                @ConstructorResult(
                                        targetClass = OrderExportDTO.class,
                                        columns = {
                                                @ColumnResult(name = "code", type = String.class),
                                                @ColumnResult(name = "creater", type = String.class),
                                                @ColumnResult(name = "customer", type = String.class),
                                                @ColumnResult(name = "bookingDay", type = Date.class),
                                                @ColumnResult(name = "checkIn", type = Date.class),
                                                @ColumnResult(name = "checkOut", type = Date.class),
                                                @ColumnResult(name = "note", type = String.class),
                                                @ColumnResult(name = "monneyCustom", type = BigDecimal.class),
                                                @ColumnResult(name = "deposit", type = BigDecimal.class),
                                                @ColumnResult(name = "vat", type = BigDecimal.class),
                                                @ColumnResult(name = "totalMoney", type = BigDecimal.class),
                                                @ColumnResult(name = "excessMoney", type = BigDecimal.class),
                                                @ColumnResult(name = "surcharge", type = BigDecimal.class),
                                                @ColumnResult(name = "discount", type = BigDecimal.class),
                                        }
                                ),
                        }
                ),
                @SqlResultSetMapping(
                        name = "orderDetailResult",
                        classes = {
                                @ConstructorResult(
                                        targetClass = OrderDetailExport.class,
                                        columns = {
                                                @ColumnResult(name = "roomName", type = String.class),
                                                @ColumnResult(name = "typeRoom", type = String.class),
                                                @ColumnResult(name = "quantity", type = Integer.class),
                                                @ColumnResult(name = "checkIn", type = Date.class),
                                                @ColumnResult(name = "checkOut", type = Date.class),
                                                @ColumnResult(name = "checkOutFake", type = Date.class),
                                                @ColumnResult(name = "unitPrice", type = BigDecimal.class),
                                                @ColumnResult(name = "totalPrice", type = BigDecimal.class),
                                                @ColumnResult(name = "timeIn", type = Integer.class)
                                        }
                                ),
                        }
                ),
        }
)
public class Order {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

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

    @Column(name = "discount_program")
    private String discountProgram;

    @Column(name = "status")
    private Integer status;

    @Column(name = "refuse_reason")
    private String refuseReason;

    @Column(name = "payment_deadline")
    private Date paymentDeadline;

    @JsonIgnore
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<HistoryTransaction> historyTransactionList;

    @JsonIgnore
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<PaymentMethod> paymentMethodList;

    @JsonIgnore
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetailList;

    @JsonIgnore
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderTimeline> orderTimelineList;

}
