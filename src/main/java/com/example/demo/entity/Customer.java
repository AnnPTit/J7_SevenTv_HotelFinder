package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "citizen_id")
    private String citizenId;

    @Column(name = "provinces")
    private String provinces;

    @Column(name = "districts")
    private String districts;

    @Column(name = "wards")
    private String wards;

    @Column(name = "passport")
    private String passport;

    @Column(name = "address")
    private String address;

    @Column(name = "nationality")
    private String nationality;

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
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Order> orderList;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Booking> bookingList;

}
