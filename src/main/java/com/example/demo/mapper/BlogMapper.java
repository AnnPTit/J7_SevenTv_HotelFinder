package com.example.demo.mapper;

import com.example.demo.dto.BlogDTO;
import com.example.demo.entity.Blog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlogMapper extends EntityMapper<BlogDTO, Blog> {

    BlogDTO toDto(Blog blog);

}
