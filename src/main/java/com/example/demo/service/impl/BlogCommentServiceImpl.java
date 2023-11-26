package com.example.demo.service.impl;

import com.example.demo.entity.BlogComment;
import com.example.demo.repository.BlogCommentRepository;
import com.example.demo.service.BlogCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogCommentServiceImpl implements BlogCommentService {
    private final BlogCommentRepository blogCommentRepository;

    @Override
    public BlogComment save(BlogComment blogComment) {
        return blogCommentRepository.save(blogComment);
    }

    @Override
    public Page<BlogComment> getPaginate(String blogId, Pageable pageable) {
        return blogCommentRepository.getComment(blogId, pageable);
    }
}
