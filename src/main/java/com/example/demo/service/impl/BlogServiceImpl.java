package com.example.demo.service.impl;

import com.example.demo.dto.BlogDTO;
import com.example.demo.entity.Blog;
import com.example.demo.mapper.BlogMapper;
import com.example.demo.repository.BlogRepository;
import com.example.demo.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    private final BlogMapper blogMapper;

    @Override
    public Blog save(Blog blog) {
        try {
            return blogRepository.save(blog);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Page<BlogDTO> getPaginate(Pageable pageable) {
//        return blogRepository.findAll(pageable);
        return blogRepository.findAll(pageable).map(blogMapper::toDto);
    }
}
