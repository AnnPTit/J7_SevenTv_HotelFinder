package com.example.demo.service.impl;

import com.example.demo.dto.BlogDTO;
import com.example.demo.entity.Blog;
import com.example.demo.repository.BlogRepository;
import com.example.demo.service.BlogService;
import com.example.demo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    private final PhotoService photoService;


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
        return blogRepository.findAll(pageable).map(item->toDto(item));
    }

    public BlogDTO toDto(Blog entity) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setId(entity.getId());
        blogDTO.setType(entity.getType());
        blogDTO.setTitle(entity.getTitle());
        blogDTO.setContent(entity.getContent());
        blogDTO.setCreateAt(entity.getCreateAt());
        blogDTO.setCreateBy(entity.getCreateBy());
        blogDTO.setUpdateAt(entity.getUpdateAt());
        blogDTO.setUpdatedBy(entity.getUpdatedBy());
        blogDTO.setCountLike(entity.getCountLike());
        blogDTO.setCountView(entity.getCountView());
        blogDTO.setStatus(entity.getStatus());
        List<String> photo = photoService.getUrlByIdBlog(entity.getId());
        if (photo.size() != 0) {
            blogDTO.setPhotoDTOS(photo);
        }
        return blogDTO;
    }
}
