package com.example.demo.dto;

import com.example.demo.entity.Floor;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Photo;
import com.example.demo.entity.TypeRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {

    private String id;
    private TypeRoom typeRoom;
    private Floor floor;
    private String roomCode;
    private String roomName;
    private String note;
    private Date createAt;
    private String createBy;
    private Date updateAt;
    private String updatedBy;
    private String deleted;
    private Integer status;
    private List<String> photoList;
    private List<OrderDetail> orderDetailList;

}
