package com.example.demo.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.demo.config.S3Util;
import com.example.demo.constant.Constant;
import com.example.demo.dto.PhotoDTO;
import com.example.demo.dto.TypeRoomDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.Photo;
import com.example.demo.entity.Room;
import com.example.demo.entity.TypeRoom;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.PhotoService;
import com.example.demo.service.TypeRoomService;
import com.example.demo.util.BaseService;
import com.example.demo.util.DataUtil;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/type-room")
@RequiredArgsConstructor
public class TypeRoomController {

    @Autowired
    private TypeRoomService typeRoomService;

    @Autowired
    private OrderRepository orderRepository;

    private final PhotoService photoService;
    private final BaseService baseService;

    private final S3Util s3Util;
    private static String bucketName = "j7v1";
    private static String accessKey = "AKIAYEQDRZP5KHP3T2EK";
    private static String secretKey = "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";
    private static String region = "us-east-1";

    @GetMapping("/getList")
    public List<TypeRoom> getList() {
        return typeRoomService.getList();
    }

    @GetMapping("/load")
    public Page<TypeRoom> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return typeRoomService.getAll(pageable);
    }

    @GetMapping("/search")
    public Page<TypeRoomDTO> findByCodeOrName(@RequestParam(name = "key") String key,
                                              @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return typeRoomService.findByCodeOrName(key, pageable);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<TypeRoom> detail(@PathVariable("id") String id) {
        TypeRoom typeRoom = typeRoomService.getTypeRoomById(id);
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<TypeRoom> save(@Valid @ModelAttribute TypeRoom typeRoom,
                                         BindingResult result,
                                         @PathParam("photos") MultipartFile[] photos) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
        if (typeRoom.getTypeRoomCode().trim().isEmpty() ||
                typeRoom.getTypeRoomName().trim().isEmpty() ||
                typeRoom.getPricePerDay() == null ||
                typeRoom.getPricePerHours() == null ||
                typeRoom.getAdult() == null ||
                typeRoom.getCapacity() == null) {
            return new ResponseEntity("Không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (typeRoomService.existsByCode(typeRoom.getTypeRoomCode())) {
            return new ResponseEntity("Mã đã tồn tại!", HttpStatus.BAD_REQUEST);
        }
        if (typeRoom.getNote().isBlank()) {
            typeRoom.setNote("No note.");
        }
        if (typeRoom.getChildren() == null) {
            typeRoom.setChildren(0);
        }
        if (typeRoom.getPricePerDay().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseEntity("Giá tiền theo ngày phải lớn hơn 0 !!", HttpStatus.BAD_REQUEST);
        }
        if (typeRoom.getPricePerHours().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseEntity("Giá tiền theo giờ phải lớn hơn 0 !!", HttpStatus.BAD_REQUEST);
        }
        if (!typeRoom.getPricePerDay().toString().matches("^\\d+(\\.\\d+)?$")) {
            return new ResponseEntity("Giá tiền theo ngày phải là một số dương !!", HttpStatus.BAD_REQUEST);
        }
        if (!typeRoom.getPricePerDay().toString().matches("^\\d+(\\.\\d+)?$")) {
            return new ResponseEntity("Giá tiền theo giờ phải là một số dương !!", HttpStatus.BAD_REQUEST);
        }
        try {
            typeRoom.setCreateAt(new Date());
            typeRoom.setUpdateAt(new Date());
            typeRoom.setStatus(Constant.COMMON_STATUS.ACTIVE);
            typeRoomService.add(typeRoom);
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
                photo.setTypeRoom(typeRoom.getId());
                photo.setCreateAt(new Date());
                photo.setUpdateAt(new Date());
                photo.setStatus(Constant.COMMON_STATUS.ACTIVE);
                photoService.add(photo);
                // Photo này của room mà Quang
            }
            System.out.println("Thêm thành công");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TypeRoom> save(@PathVariable("id") String id,
                                         @Valid @ModelAttribute TypeRoom typeRoom,
                                         BindingResult result,
                                         @PathParam("photos") MultipartFile[] photos) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
        if (typeRoom.getTypeRoomCode().trim().isEmpty() ||
                typeRoom.getTypeRoomName().trim().isEmpty() ||
                typeRoom.getPricePerDay() == null ||
                typeRoom.getPricePerHours() == null ||
                typeRoom.getAdult() == null ||
                typeRoom.getCapacity() == null) {
            return new ResponseEntity("Không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (typeRoom.getNote().isBlank()) {
            typeRoom.setNote("No note.");
        }
        if (typeRoom.getChildren() == null) {
            typeRoom.setChildren(0);
        }
        if (typeRoom.getPricePerDay().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseEntity("Giá tiền theo ngày phải lớn hơn 0 !!", HttpStatus.BAD_REQUEST);
        }
        if (typeRoom.getPricePerHours().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseEntity("Giá tiền theo giờ phải lớn hơn 0 !!", HttpStatus.BAD_REQUEST);
        }
        if (!typeRoom.getPricePerDay().toString().matches("^\\d+(\\.\\d+)?$")) {
            return new ResponseEntity("Giá tiền theo ngày phải là một số dương !!", HttpStatus.BAD_REQUEST);
        }
        if (!typeRoom.getPricePerDay().toString().matches("^\\d+(\\.\\d+)?$")) {
            return new ResponseEntity("Giá tiền theo giờ phải là một số dương !!", HttpStatus.BAD_REQUEST);
        }
        TypeRoom existingTypeRoom = typeRoomService.getTypeRoomById(id);
        existingTypeRoom.setTypeRoomCode(typeRoom.getTypeRoomCode());
        existingTypeRoom.setTypeRoomName(typeRoom.getTypeRoomName());
        existingTypeRoom.setPricePerDay(typeRoom.getPricePerDay());
        existingTypeRoom.setPricePerHours(typeRoom.getPricePerHours());
        existingTypeRoom.setCapacity(typeRoom.getCapacity());
        existingTypeRoom.setAdult(typeRoom.getAdult());
        existingTypeRoom.setChildren(typeRoom.getChildren());
        existingTypeRoom.setNote(typeRoom.getNote());
        existingTypeRoom.setUpdateAt(new Date());
        typeRoomService.add(existingTypeRoom);
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
                    photo.setTypeRoom(typeRoom.getId());
                    photo.setCreateAt(new Date());
                    photo.setUpdateAt(new Date());
                    photo.setStatus(Constant.COMMON_STATUS.ACTIVE);
                    photoService.add(photo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        TypeRoom typeRoom = typeRoomService.getTypeRoomById(id);
        // Lấy danh sách phòng
        List<Room> list = typeRoom.getRoomList();
        for (Room room : list) {
            List<Order> listR = orderRepository.getRoomInOrder(room.getId());
            if (listR.size() != 0) {
                return new ResponseEntity<String>("Không thể xóa loại phòng vì phòng đang nằm trong hóa đơn", HttpStatus.BAD_REQUEST);
            }
        }
        typeRoom.setStatus(Constant.COMMON_STATUS.UNACTIVE);
        typeRoomService.add(typeRoom);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<List<PhotoDTO>> getBlogImages(@PathVariable("id") String id) {
        List<PhotoDTO> photoDTOs = new ArrayList<>();
        List<Photo> blogPhotos = photoService.getPhotoByTypeRoom(id);

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
