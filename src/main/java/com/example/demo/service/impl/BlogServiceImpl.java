package com.example.demo.service.impl;

import com.example.demo.constant.Constant;
import com.example.demo.dto.BlogDTO;
import com.example.demo.entity.Blog;
import com.example.demo.entity.BlogComment;
import com.example.demo.entity.BlogLike;
import com.example.demo.repository.BlogLikeRepository;
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

    private final BlogLikeRepository blogLikeRepository;

//    private final BLo blogLikeRepository;


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
        return blogRepository.loadAndSearch(null, pageable).map(item -> toDto(item));
    }

    @Override
    public Page<BlogDTO> loadAndSearch(String title, Pageable pageable) {
        return blogRepository.loadAndSearch((title != null && !title.isEmpty()) ? "%" + title + "%" : null, pageable).map(item -> toDto(item));
    }


    @Override
    public void like(String blogId, String customerId) {
        BlogLike blogLike = new BlogLike();
        blogLike.setBlog(blogId);
        if (customerId != null) {
            blogLike.setCustomer(customerId);
        }
        blogLike.setStatus(Constant.COMMON_STATUS.ACTIVE);
        blogLikeRepository.save(blogLike);
    }

    @Override
    public void unLike(String blogId, String customId) {
        if (customId == null) {
            List<BlogLike> likeList = blogLikeRepository.anonymousLike(blogId);
            if (likeList.size() != 0) {
                BlogLike blogLike = likeList.get(0);
                blogLikeRepository.delete(blogLike);
            }
        } else {
            List<BlogLike> likeList = blogLikeRepository.customLike(blogId, customId);
            if (likeList.size() != 0) {
                BlogLike blogLike = likeList.get(0);
                blogLikeRepository.delete(blogLike);
            }
        }

    }

    @Override
    public Blog findOne(String blogId) {
        return blogRepository.getOne(blogId);
    }

    @Override
    public Blog findById(String id) {
        return blogRepository.findById(id).orElse(null);
    }

    @Override
    public Integer countLike(String blogId) {
        return blogLikeRepository.countLike(blogId);
    }

    @Override
    public void updateView(String blogId, Integer view) {
        blogRepository.updateView(blogId, view);
    }

    @Override
    public void updateLike(String blogId, Integer like) {
        blogRepository.updateLike(blogId, like);
    }

    @Override
    public Integer countView(String blogId) {
        Blog blog = blogRepository.getOne(blogId);
        if (blog != null) {
            blog.setCountView(blog.getCountView() + 1);
            blogRepository.save(blog);
            return blog.getCountView();
        }
        return 0;
    }


    @Override
    public Page<BlogComment> getComment(String blogId) {
        return null;
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
