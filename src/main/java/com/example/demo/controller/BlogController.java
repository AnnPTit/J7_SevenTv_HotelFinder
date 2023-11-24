package com.example.demo.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.demo.config.S3Util;
import com.example.demo.constant.Constant;
import com.example.demo.dto.BlogDTO;
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

    @PostMapping("/save")
    public ResponseEntity<Blog> save(@Valid @ModelAttribute Blog blog,
                                     BindingResult bindingResult,
                                     @PathParam("photos") MultipartFile[] photos) {
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errorMap = new HashMap<>();
//            for (FieldError error : bindingResult.getFieldErrors()) {
//                String key = error.getField();
//                String value = error.getDefaultMessage();
//                errorMap.put(key, value);
//            }
//            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
//        }


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
                photo.setBlog(blog);
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


}
