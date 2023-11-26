package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogCommentDTO {
    private String id;
    private String username;
    private String content;
    private String idBlog;
    private Date createdAt;
    private Boolean  isCreate;
    private Integer currentPage;

}
