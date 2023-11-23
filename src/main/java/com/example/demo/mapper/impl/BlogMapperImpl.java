package com.example.demo.mapper.impl;

import com.example.demo.dto.BlogDTO;
import com.example.demo.entity.Blog;
import com.example.demo.mapper.BlogMapper;
import com.example.demo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//@RequiredArgsConstructor
//public class BlogMapperImpl implements BlogMapper {
//
//    private final PhotoService photoService;
//
//    @Override
//    public Blog toEntity(BlogDTO dto) {
//        return null;
//    }
//
//    @Override
//    public BlogDTO toDto(Blog entity) {
//        BlogDTO blogDTO = new BlogDTO();
//        System.out.println("DDDDDDD");
//        blogDTO.setId(entity.getId());
//        blogDTO.setType(entity.getType());
//        blogDTO.setTitle(entity.getTitle());
//        blogDTO.setContent(entity.getContent());
//        blogDTO.setCreateAt(entity.getCreateAt());
//        blogDTO.setCreateBy(entity.getCreateBy());
//        blogDTO.setUpdateAt(entity.getUpdateAt());
//        blogDTO.setUpdatedBy(entity.getUpdatedBy());
//        blogDTO.setStatus(entity.getStatus());
//        blogDTO.setCountLike(10000);
//        List<String> photo = photoService.getUrlByIdBlog(entity.getId());
//        if (photo.size() != 0) {
//            blogDTO.setPhotoDTOS(photo);
//        }
//        return blogDTO;
//    }
//
//    @Override
//    public List<Blog> toEntity(List<BlogDTO> dtoList) {
//        return null;
//    }
//
//    @Override
//    public List<BlogDTO> toDto(List<Blog> entityList) {
//        List<BlogDTO> blogDTOS = new ArrayList<>();
//        for (Blog blog : entityList) {
//            blogDTOS.add(toDto(blog));
//        }
//        return blogDTOS;
//    }
//}
