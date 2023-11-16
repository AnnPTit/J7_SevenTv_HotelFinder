package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "service")
public class Service extends AbstractAuditingEntity<String> implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @NotBlank(message = "Service code  is required")
    @Column(name = "service_code")
    private String serviceCode;

    @NotBlank(message = "Service name   is required")
    @Column(name = "service_name")
    private String serviceName;

    @NotNull(message = "Price is not null")
    @Min(value = 1 , message = "Đơn giá lớn hơn hoặc bằng 1 ")
    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "description")
    private String description;

//    @Column(name = "create_at")
//    private Date createAt;
//
//    @Column(name = "create_by")
//    private String createBy;
//
//    @Column(name = "update_at")
//    private Date updateAt;
//
//    @Column(name = "updated_by")
//    private String updatedBy;

    @Column(name = "deleted")
    private String deleted;

    @Column(name = "status")
    private Integer status;

    @JsonIgnore
    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<ServiceUsed> serviceUsedList;

    @JsonIgnore
    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<ComboService> comboServiceList;

}
