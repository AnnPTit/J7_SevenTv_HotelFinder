package com.example.demo.service.impl;

import com.example.demo.entity.Blog;
import com.example.demo.repository.BlogRepository;
import com.example.demo.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    @Override
    public Blog save(Blog blog) {
        try {
            return blogRepository.save(blog);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
