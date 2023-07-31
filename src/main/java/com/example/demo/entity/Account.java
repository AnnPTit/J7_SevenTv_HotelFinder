package com.example.demo.entity;

import com.example.demo.validator.MinAge;
import com.example.demo.validator.UniqueCitizenId;
import com.example.demo.validator.UniqueEmail;
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

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "account")
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Column(name = "account_code")
    private String accountCode;

    @Column(name = "password")
    private String password;

    @Column(name = "fullname")
    @NotBlank(message = "Họ tên không được để trống!!")
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
    @UniqueEmail
    private String email;

    @Column(name = "phone_number")
    @NotBlank(message = "Số điện thoại không được để trống!!")
    @Size(min = 10, max = 10, message = "Số điện thoại phải có 10 chữ số!!")
    @Pattern(regexp = "^(\\+84|0)[35789][0-9]{8}$", message = "Số điện thoại không đúng định dạng!!")
    private String phoneNumber;

    @Column(name = "citizen_id")
    @NotBlank(message = "Căn cước công dân không được để trống!!")
    @Pattern(regexp = "\\d{12}", message = "Căn cước công dân không đúng định dạng!!")
    @UniqueCitizenId
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
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Order> orderList;

}
