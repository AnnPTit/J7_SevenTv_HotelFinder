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

import java.awt.*;
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
@Table(name = "service")
public class Service {
    @Id
    @Column(name = "id")
    @GenericGenerator(name = "ganerator", strategy = "uuid2", parameters = {})
    @GeneratedValue(generator = "ganerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "service_code")
    private String serviceCode;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "description")
    private String description;

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

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<ServiceUsed> serviceUsedList;

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<ComboService> comboServiceList;

}
