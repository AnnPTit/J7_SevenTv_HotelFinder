package com.example.demo.controller;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.demo.config.S3Util;
import com.example.demo.entity.Photo;
import com.example.demo.entity.Room;
import com.example.demo.service.PhotoService;
import com.example.demo.service.RoomService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private RoomService roomService;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private S3Util s3Util;

    private static String bucketName = "j7v1"; // Thay bằng tên bucket AWS S3 của bạn
    int expirationInSeconds = 3600; // Thời gian hết hạn URL (tính bằng giây)

    private static String accessKey = "AKIAYEQDRZP5KHP3T2EK";
    private static String secretKey = "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";
    private static String region = "us-east-1"; // Ví dụ: "ap-southeast-1"

    @PostMapping("upload")
    public void uploadFile(@RequestParam("file") MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                File fileObj = convertMultiPartToFile(file);
                String key = "AnDz" + file.getOriginalFilename();
                s3Util.uploadPhoto(key, fileObj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    @GetMapping("getPublicUrl")
    public ResponseEntity<String> getPublicUrl(@RequestParam("fileName") String fileName) {


        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();


        // Tạo yêu cầu URL công khai
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
                .withMethod(HttpMethod.GET);
//                .withExpiration(getExpirationTime(expirationInSeconds));

        // Lấy URL công khai
        URL publicUrl = s3Client.generatePresignedUrl(urlRequest);

        // Trả về URL công khai dưới dạng chuỗi
        return ResponseEntity.ok(publicUrl.toString());
    }


    @PostMapping("/save")
    public ResponseEntity<Room> save(@ModelAttribute Room room, @PathParam("photos") MultipartFile[] photos) {

//        List<String> listURL = new ArrayList<>();
        try {
            roomService.add(room);
            for (MultipartFile file : photos) {
                File fileObj = convertMultiPartToFile(file);
                String key = "AnDz" + file.getOriginalFilename();
                s3Util.uploadPhoto(key, fileObj);
                BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .build();


//                // Tạo yêu cầu URL công khai
//                GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, key)
//                        .withMethod(HttpMethod.GET);
////                .withExpiration(getExpirationTime(expirationInSeconds));
//
//                // Lấy URL công khai
//                URL publicUrl = s3Client.generatePresignedUrl(urlRequest);


                //
                String imageUrl = s3Client.getUrl(bucketName, key).toString();
                System.out.println(imageUrl);

//                listURL.add(imageUrl);
                Photo photo = new Photo();
                photo.setUrl(imageUrl);
                photo.setRoom(room);
                photo.setCreateAt(new Date());
                photo.setStatus(1);
                photoService.add(photo);

            }
            System.out.println("Them Thanh cong ");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }


    // Phương thức để tính thời gian hết hạn URL
//    private static Date getExpirationTime(int expirationInSeconds) {
//        long expirationInMillis = System.currentTimeMillis() + (expirationInSeconds * 1000);
//        return new Date(expirationInMillis);
//}


    @GetMapping("/load")
    public Page<Room> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.getAll(pageable);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Room> detail(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Room> update(@PathVariable("id") String id, @RequestBody Room room) {
        room.setId(id);
        room.setUpdateAt(new Date());
        roomService.add(room);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        room.setStatus(0);
        roomService.add(room);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

//    private void savePicture(@ModelAttribute Room room,
//                             @PathParam("photos") MultipartFile[] photos,
//                             List<Photo> photoList) {
//        if (photos != null && photos.length > 0) {
//            // Save the Room entity first
//            room.setCreateAt(new Date());
//            room.setUpdateAt(new Date());
//            room.setStatus(1);
//            roomService.add(room);
//
//            for (MultipartFile photoFile : photos) {
//                if (!photoFile.isEmpty()) {
//                    try {
//                        String fileName = photoFile.getOriginalFilename();
//                        String filePath = "D:/J7_HangNT169/src/main/resources/static/assets/img/room/" + fileName;
//                        String filePathForSql = "/assets/img/room/" + fileName;
//                        File dest = new File(filePath);
//                        // Luu hinh anh vao thu muc
//                        photoFile.transferTo(dest);
//                        System.out.println(filePath);
//                        // Tao doi tuong hinh
//                        Photo photo = new Photo();
//                        String id = UUID.randomUUID().toString();
//                        photo.setId(id);
//                        // Set the managed Room entity to the Photo
//                        photo.setRoom(room);
//                        photo.setUrl(filePathForSql);
//                        photo.setCreateAt(new Date());
//                        photo.setUpdateAt(new Date());
//                        photo.setStatus(1);
//                        // add hinh vao list
//                        photoList.add(photo);
//                    } catch (Exception e) {
//                        // Xử lý lỗi khi lưu file
//                        e.printStackTrace();
//                    }
//                }
//            }
//            // Save the list of Photo entities
//            photoService.save(photoList);
//        }
}

