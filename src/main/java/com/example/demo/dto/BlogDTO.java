package com.example.demo.dto;

import com.example.demo.entity.Photo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogDTO {
    private String id;
    private String type;
    private String title;
    private String content;
    private Integer countLike;
    private Integer countView;
    private Date createAt;
    private String createBy;
    private Date updateAt;
    private String updatedBy;
    private Integer status;
    private List<String> photoDTOS;
}
