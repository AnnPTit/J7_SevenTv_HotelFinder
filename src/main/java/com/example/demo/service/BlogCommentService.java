package com.example.demo.service;

import com.example.demo.entity.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogCommentService {
    BlogComment save(BlogComment blogComment);

    Page<BlogComment> getPaginate(String blogId, Pageable pageable);
}
