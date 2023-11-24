package com.example.demo.service;

import com.example.demo.dto.BlogDTO;
import com.example.demo.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogService {
    Blog save(Blog blog);

    Page<BlogDTO> getPaginate(Pageable pageable);

}
