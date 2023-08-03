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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "combo")
public class Combo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Combo code  is required")
    @Column(name = "combo_code")
    private String comboCode;

    @NotBlank(message = "Combo name  is required")
    @Column(name = "combo_name")
    private String comboName;

    @NotNull(message = "Price is not null")
    @Min(1)
    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "note")
    private String note;

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

/*    @JsonIgnore*/
    @OneToMany(mappedBy = "combo", fetch = FetchType.LAZY)
    private List<ComboService> comboServiceList;

    @JsonIgnore
    @OneToMany(mappedBy = "combo", fetch = FetchType.LAZY)
    private List<ComboUsed> comboUsedList;

}
