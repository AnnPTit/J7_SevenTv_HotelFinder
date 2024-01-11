package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "booking")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_type_room")
    private TypeRoom typeRoom;

    @ManyToOne
    @JoinColumn(name = "id_customer")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "id_order")
    private Order order;

    @Column(name = "note")
    private String note;

    @Column(name = "number_rooms")
    private Integer numberRooms;

    @Column(name = "number_adults")
    private Integer numberAdults;

    @Column(name = "number_children")
    private Integer numberChildren;

    @Column(name = "number_days")
    private Integer numberDays;

    @Column(name = "check_in_date")
    private Date checkInDate;

    @Column(name = "check_out_date")
    private Date checkOutDate;

    @Column(name = "room_price")
    private BigDecimal roomPrice;

    @Column(name = "vat")
    private BigDecimal vat;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "bank_account_name")
    private String bankAccountName;

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

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "url")
    private String url;

//    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
//    private List<BookingHistoryTransaction> list;
}
