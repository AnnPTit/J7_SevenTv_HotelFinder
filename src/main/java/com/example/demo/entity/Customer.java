package com.example.demo.entity;

import com.example.demo.validator.MinAge;
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
import jakarta.validation.constraints.Pattern;
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
    private String customerCode;

    @Column(name = "username")
    @NotBlank(message = "Username không được để ")
    private String username;

    @Column(name = "password")
    @Size(min = 5, max = 20, message = "Password phải từ 5-20 kí ")
    @NotBlank(message = "Password không được bỏ trống")
    private String password;

    @Column(name = "fullname")
    @NotBlank(message = "Full name không được bỏ trống")
    private String fullname;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "birthday")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Ngày sinh không được để tróng!!")
    @MinAge(value = 18, message = "Bạn phải ít nhất 18 tuổi!!")
    private Date birthday;

    @Column(name = "email")
    @NotBlank(message = "Email không được để trống!!")
    @Email(message = "Email không đúng định dạng!!")
    private String email;

    @Column(name = "phone_number")
    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Size(min = 10, max = 10, message = "Số điện thoại phải có 10 chữ số!!")
//    @Pattern(regexp = "^(\\+84|0)[35789][0-9]{8}$", message = "Số điện thoại không đúng định dạng!!")
    private String phoneNumber;

    @Column(name = "citizen_id")
    @NotBlank(message = "Căn cước công dân không được để trống!!")
//    @Pattern(regexp = "\\d{12}", message = "Căn cước công dân không đúng định dạng!!")
    private String citizenId;

    @Column(name = "provinces")
    private String provinces;

    @Column(name = "districts")
    private String districts;

    @Column(name = "wards")
    private String wards;

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
