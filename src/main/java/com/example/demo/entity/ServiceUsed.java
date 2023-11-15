package com.example.demo.entity;

import com.example.demo.dto.OrderDetailExport;
import com.example.demo.dto.OrderExportDTO;
import com.example.demo.dto.ServiceUsedInvoiceDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "service_used")
@SqlResultSetMappings(
        value = {
                @SqlResultSetMapping(
                        name = "serviceResult",
                        classes = {
                                @ConstructorResult(
                                        targetClass = ServiceUsedInvoiceDTO.class,
                                        columns = {
                                                @ColumnResult(name = "roomName1", type = String.class),
                                                @ColumnResult(name = "service", type = String.class),
                                                @ColumnResult(name = "quantity2", type = Integer.class),
                                                @ColumnResult(name = "price", type = BigDecimal.class),
                                                @ColumnResult(name = "total2", type = BigDecimal.class),
                                        }
                                ),
                        }
                ),
        }
)
public class ServiceUsed {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne
    @JoinColumn(name = "order_detail_id", nullable = false)
    private OrderDetail orderDetail;

    @Column(name = "quantity")
    private Integer quantity;

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

}
