package com.example.demo.entity;

import com.example.demo.dto.RoomRequestDTO;
import com.example.demo.dto.RoomResponeDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
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
