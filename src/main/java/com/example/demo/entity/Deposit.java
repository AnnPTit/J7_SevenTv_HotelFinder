package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "deposit")
public class Deposit {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "pile_value")
    private Integer pileValue;

    @Column(name = "unit")
    private Integer unit;

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
