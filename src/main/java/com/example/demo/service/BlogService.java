package com.example.demo.service;

import com.example.demo.dto.BlogDTO;
import com.example.demo.entity.Blog;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogService {
    Blog save(Blog blog);

    Page<BlogDTO> getPaginate(Pageable pageable);

    void like(String blogId, String customerId);

    Blog findOne(String blogId);

    Integer countLike(String blogId);

    void updateView(String blogId, Integer view);

    void updateLike(String blogId, Integer like);

    Integer countView(String blogId);

    void unLike(String blogId, String customId);

}
