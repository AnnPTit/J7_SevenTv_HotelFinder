package com.example.demo.entity;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.RoomResponeDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
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
@Table(name = "room")
@SqlResultSetMappings(
        value = {
                @SqlResultSetMapping(
                        name = "roomResult",
                        classes = {
                                @ConstructorResult(
                                        targetClass = RoomResponeDTO.class,
                                        columns = {
                                                @ColumnResult(name = "id", type = String.class),
                                                @ColumnResult(name = "roomCode", type = String.class),
                                                @ColumnResult(name = "roomName", type = String.class),
                                                @ColumnResult(name = "note", type = String.class),
                                                @ColumnResult(name = "typeRoom", type = String.class),
                                                @ColumnResult(name = "capacity", type = Integer.class),
                                                @ColumnResult(name = "pricePerHours", type = BigDecimal.class),
                                                @ColumnResult(name = "pricePerDay", type = BigDecimal.class),
                                        }
                                ),
                        }
                ),
                @SqlResultSetMapping(
                        name = "cartResult",
                        classes = {
                                @ConstructorResult(
                                        targetClass = CartDTO.class,
                                        columns = {
                                                @ColumnResult(name = "roomId", type = String.class),
                                                @ColumnResult(name = "roomName", type = String.class),
                                                @ColumnResult(name = "typeRoom", type = String.class),
                                                @ColumnResult(name = "bookingStart", type = Date.class),
                                                @ColumnResult(name = "bookingEnd", type = Date.class),
                                                @ColumnResult(name = "price", type = BigDecimal.class),
                                                @ColumnResult(name = "numberCustom", type = Integer.class),
                                                @ColumnResult(name = "orderStatus", type = Integer.class),
                                                @ColumnResult(name = "bookingDay", type = Date.class),
                                                @ColumnResult(name = "url", type = String.class),
                                        }
                                ),
                        }
                ),
        }
)

public class Room {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "type_room_id")
    private TypeRoom typeRoom;

    @ManyToOne
    @JoinColumn(name = "floor_id")
    private Floor floor;

    @Column(name = "room_code")
    private String roomCode;

    @Column(name = "room_name")
    private String roomName;

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

    //    @JsonManagedReference
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Photo> photoList;

    @JsonIgnore
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetailList;

}
