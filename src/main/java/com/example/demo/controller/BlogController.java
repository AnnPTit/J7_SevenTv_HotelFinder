package com.example.demo.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.demo.config.S3Util;
import com.example.demo.constant.Constant;
import com.example.demo.dto.BlogDTO;
import com.example.demo.dto.PhotoDTO;
import com.example.demo.entity.*;
import com.example.demo.service.BlogService;
import com.example.demo.service.PhotoService;
import com.example.demo.util.BaseService;
import com.example.demo.util.DataUtil;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final PhotoService photoService;
    private final BaseService baseService;

    private final S3Util s3Util;
    private static String bucketName = "j7v1";
    private static String accessKey = "AKIAYEQDRZP5KHP3T2EK";
    private static String secretKey = "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";
    private static String region = "us-east-1";


    @GetMapping("/load")
    public Page<BlogDTO> load(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return blogService.getPaginate(pageable);
    }

    @GetMapping("/loadAndSearch")
    public Page<BlogDTO> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                       @RequestParam(name = "current_page", defaultValue = "0") int current_page
    ) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return blogService.loadAndSearch(key, pageable);
    }

    @PostMapping("/save")
    public ResponseEntity<Blog> save(@Valid @ModelAttribute Blog blog,
                                     BindingResult bindingResult,
                                     @PathParam("photos") MultipartFile[] photos) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
        if (blog.getTitle().trim().isEmpty()) {
            return new ResponseEntity("Tiêu đề không được để trống!!", HttpStatus.BAD_REQUEST);
        }
        if (blog.getContent().trim().isEmpty()) {
            return new ResponseEntity("Nội dung hông được để trống!!", HttpStatus.BAD_REQUEST);
        }
        try {
            blog.setCreateAt(new Date());
            blog.setUpdateAt(new Date());
            blog.setCreateBy(baseService.getCurrentUser().getFullname());
            blog.setUpdatedBy(baseService.getCurrentUser().getFullname());
            blog.setUpdateAt(new Date());
            blog.setCountView(0);
            blog.setCountLike(0);
            blog.setStatus(Constant.COMMON_STATUS.ACTIVE);
            blogService.save(blog);
            for (MultipartFile file : photos) {
                File fileObj = DataUtil.convertMultiPartToFile(file);
                String key = "AnDz" + file.getOriginalFilename();
                s3Util.uploadPhoto(key, fileObj);
                BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .build();
                String imageUrl = s3Client.getUrl(bucketName, key).toString();
                System.out.println(imageUrl);

                Photo photo = new Photo();
                photo.setUrl(imageUrl);
                photo.setBlog(blog.getId());
                photo.setCreateAt(new Date());
                photo.setUpdateAt(new Date());
                photo.setStatus(Constant.COMMON_STATUS.ACTIVE);
                photoService.add(photo);
            }

            System.out.println("Thêm thành công");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Blog>(blog, HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Blog> detail(@PathVariable("id") String id) {
        Blog blog = blogService.findOne(id);
        return new ResponseEntity<Blog>(blog, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Blog> update(@PathVariable("id") String id, @Valid @ModelAttribute Blog blog,
                                       BindingResult bindingResult,
                                       @PathParam("photos") MultipartFile[] photos) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
//        if (blog.getTitle().trim().isEmpty()) {
//            return new ResponseEntity("Tiêu đề không được để trống!!", HttpStatus.BAD_REQUEST);
//        }
//        if (blog.getContent().trim().isEmpty()) {
//            return new ResponseEntity("Nội dung hông được để trống!!", HttpStatus.BAD_REQUEST);
//        }
        Blog existingBlog = blogService.findOne(id);

        if (existingBlog == null) {
            return new ResponseEntity("Không tìm thấy blog có ID: " + id, HttpStatus.NOT_FOUND);
        }
        // Cập nhật chỉ những trường cần thiết
        existingBlog.setTitle(blog.getTitle());
        existingBlog.setContent(blog.getContent());
        existingBlog.setUpdateAt(new Date());
        existingBlog.setUpdatedBy(baseService.getCurrentUser().getFullname());
        blogService.save(existingBlog);
        try {
            if (photos != null) {
                for (MultipartFile file : photos) {
                    File fileObj = DataUtil.convertMultiPartToFile(file);
                    String key = "AnDz" + file.getOriginalFilename();
                    s3Util.uploadPhoto(key, fileObj);
                    BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                    AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                            .withRegion(region)
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .build();
                    String imageUrl = s3Client.getUrl(bucketName, key).toString();
                    System.out.println(imageUrl);

                    Photo photo = new Photo();
                    photo.setUrl(imageUrl);
                    photo.setBlog(blog.getId());
                    photo.setCreateAt(new Date());
                    photo.setUpdateAt(new Date());
                    photo.setStatus(Constant.COMMON_STATUS.ACTIVE);
                    photoService.add(photo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Blog>(blog, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Blog> delete(@PathVariable("id") String id) {
        Blog blog = blogService.findById(id);
        blog.setStatus(Constant.COMMON_STATUS.UNACTIVE);
        blogService.save(blog);
        return new ResponseEntity("Deleted " + id + " successfully", HttpStatus.OK);
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<List<PhotoDTO>> getBlogImages(@PathVariable("id") String id) {
        List<PhotoDTO> photoDTOs = new ArrayList<>();
        List<Photo> blogPhotos = photoService.getPhotoByBlog(id);

        for (Photo photo : blogPhotos) {
            PhotoDTO photoDTO = new PhotoDTO();
            System.out.println("Id: " + photo.getId());
            photoDTO.setId(photo.getId());
            photoDTO.setUrl(photo.getUrl());
            photoDTOs.add(photoDTO);
        }
        return new ResponseEntity<>(photoDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/delete-photo/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable("id") String id) {
        System.out.println("Delete url: " + id);
        photoService.deletePhotoById(id);
        return new ResponseEntity("Photo deleted successfully", HttpStatus.OK);
    }
}
