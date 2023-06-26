package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@Entity
@Table(name = "service_type")
public class ServiceType {
    @Id
    @Column(name = "id")
    @GenericGenerator(name = "ganerator", strategy = "uuid2", parameters = {})
    @GeneratedValue(generator = "ganerator")
    private UUID id;

    @Column(name = "service_type_code")
    private String serviceTypeCode;

    @Column(name = "service_type_name")
    private String serviceTypeName;

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

    @OneToMany(mappedBy = "serviceType", fetch = FetchType.LAZY)
    private List<Service> serviceList;


}
