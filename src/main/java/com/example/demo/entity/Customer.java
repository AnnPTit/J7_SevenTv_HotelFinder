package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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
    @NotBlank(message = "Customer code is required")
    private String customerCode;

    @Column(name = "username")
    @NotBlank(message = "Username is required")
    private String username;

    @Column(name = "password")
    @Size(min = 5, max = 20, message = "Password must be between 5 and 20 characters")
    @NotBlank(message = "Password is required")
    private String password;

    @Column(name = "fullname")
    @NotBlank(message = "Full name isn't empty")
    private String fullname;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "birthday")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Birthday is required")
    private Date birthday;

    @Column(name = "email")
    @Email(message = "Invalid email")
    private String email;

    @Column(name = "phone_number")
    @NotBlank(message = "Phone Number isn't empty")
    private String phoneNumber;

    @Column(name = "address")
    @NotBlank(message = "Address isn't empty")
    private String address;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "citizen_id")
    private String citizenId;

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


}
