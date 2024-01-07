package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

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

    @Column(name = "id_type_room")
    private String idTypeRoom;

    @Column(name = "id_customer")
    private String idCustomer;

    @Column(name = "id_order")
    private String idOrder;

    @Column(name = "note")
    private String note;

    @Column(name = "numberRooms")
    private Integer numberRooms;

    @Column(name = "numberCustomers")
    private Integer numberCustomers;

    @Column(name = "numberAdults")
    private Integer numberAdults;

    @Column(name = "numberChildren")
    private Integer numberChildren;

    @Column(name = "numberDays")
    private Integer numberDays;

    @Column(name = "check_in_date")
    private Date checkInDate;

    @Column(name = "check_out_date")
    private Date checkOutDate;

    @Column(name = "roomPrice")
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
}
