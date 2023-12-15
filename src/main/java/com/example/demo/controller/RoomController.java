package com.example.demo.controller;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.demo.config.S3Util;
import com.example.demo.constant.Constant;
import com.example.demo.dto.PhotoDTO;
import com.example.demo.dto.RoomDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.FacilityService;
import com.example.demo.service.PhotoService;
import com.example.demo.service.RoomFacilityService;
import com.example.demo.service.RoomService;
import com.example.demo.util.DataUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/room")
public class RoomController {

    @Autowired
    private RoomService roomService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private RoomFacilityService roomFacilityService;
    @Autowired
    private S3Util s3Util;

    private static String bucketName = "j7v1"; // Thay bằng tên bucket AWS S3 của bạn
    int expirationInSeconds = 3600; // Thời gian hết hạn URL (tính bằng giây)

    private static String accessKey = "AKIAYEQDRZP5KHP3T2EK";
    private static String secretKey = "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";
    private static String region = "us-east-1"; // Ví dụ: "ap-southeast-1"

    @GetMapping("/getAllByStatus")
    public List<Room> getAllByStatus() {
        return roomService.getAllByStatus(Constant.COMMON_STATUS.ACTIVE);
    }

    @GetMapping("/getList")
    public List<Room> getList() {
        return roomService.getList();
    }

    @GetMapping("/load")
    public Page<Room> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.getAll(pageable);
    }

    @GetMapping("/topRoom")
    public List<Room> getTopRoom() {
        return roomService.getTopRoom();
    }

//    @GetMapping("/room-plan")
//    public List<List<RoomDTO>> getRoomsByFloorsAscendingOrder() {
//        List<List<RoomDTO>> roomDTOS = new ArrayList<>();
//        List<List<Room>> list = roomService.getRoomsByAllFloors();
//        for (List<Room> roomList : list) {
//            List<RoomDTO> roomDTOList = new ArrayList<>();
//            for (Room room : roomList) {
//                RoomDTO roomDTO = new RoomDTO();
//                roomDTO.setId(room.getId());
//                roomDTO.setFloor(room.getFloor());
//                roomDTO.setTypeRoom(room.getTypeRoom());
//                roomDTO.setRoomCode(room.getRoomCode());
//                roomDTO.setRoomName(room.getRoomName());
//                roomDTO.setNote(room.getNote());
//                roomDTO.setCreateAt(room.getCreateAt());
//                roomDTO.setCreateBy(room.getCreateBy());
//                roomDTO.setUpdateAt(room.getUpdateAt());
//                roomDTO.setUpdatedBy(room.getUpdatedBy());
//                roomDTO.setDeleted(room.getDeleted());
//                List<String> roomImages = room.getPhotoList()
//                        .stream()
//                        .map(Photo::getUrl)
//                        .collect(Collectors.toList());
//                List<OrderDetail> orderDetailList = room.getOrderDetailList();
//                roomDTO.setPhotoList(roomImages);
//                roomDTO.setOrderDetailList(orderDetailList);
//                roomDTO.setStatus(room.getStatus());
//                roomDTOList.add(roomDTO);
//            }
//            roomDTOS.add(roomDTOList);
//        }
////        Collections.reverse(roomsByAllFloors);
//        return roomDTOS;
//    }

    @GetMapping("/loadAndSearch")
    public Page<Room> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                    @RequestParam(name = "floorId", defaultValue = "") String floorId,
                                    @RequestParam(name = "typeRoomId", defaultValue = "") String typeRoomId,
                                    @RequestParam(name = "current_page", defaultValue = "0") int current_page
    ) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.loadAndSearch(key, key, floorId, typeRoomId, pageable);
    }

    @GetMapping("/loadAndSearchBookRoom")
    public List<Room> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                    @RequestParam(name = "floorId", defaultValue = "") String floorId,
                                    @RequestParam(name = "typeRoomId", defaultValue = "") String typeRoomId,
                                    @RequestParam(name = "start", defaultValue = "0") BigDecimal start,
                                    @RequestParam(name = "end", defaultValue = "100000000") BigDecimal end,
                                    @RequestParam(name = "dayStart", defaultValue = "") String dayStart,
                                    @RequestParam(name = "dayEnd", defaultValue = "") String dayEnd) {
        Date startDay = null;
        Date endDay = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (!dayStart.isEmpty()) {
                startDay = dateFormat.parse(dayStart);
            }

            if (!dayEnd.isEmpty()) {
                endDay = dateFormat.parse(dayEnd);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return roomService.loadAndSearchBookRoom(key, key, floorId, typeRoomId, start, end, startDay, endDay);
    }

    @GetMapping("/loadByCondition")
    public List<Room> loadByCondition(@RequestParam(name = "typeRoomId", defaultValue = "") String typeRoomId,
                                      @RequestParam(name = "capacity", defaultValue = "0") Integer capacity,
                                      @RequestParam(name = "adult", defaultValue = "0") Integer adult,
                                      @RequestParam(name = "children", defaultValue = "0") Integer children,
                                      @RequestParam(name = "dayStart", defaultValue = "") String dayStart,
                                      @RequestParam(name = "dayEnd", defaultValue = "") String dayEnd) {
        Date startDay = null;
        Date endDay = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (!dayStart.isEmpty()) {
                startDay = dateFormat.parse(dayStart);
            }

            if (!dayEnd.isEmpty()) {
                endDay = dateFormat.parse(dayEnd);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return roomService.loadRoomByCondition(typeRoomId, capacity, adult, children, startDay, endDay);
    }

    @PostMapping("upload")
    public void uploadFile(@RequestParam("file") MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                File fileObj = DataUtil.convertMultiPartToFile(file);
                String key = "AnDz" + file.getOriginalFilename();
                s3Util.uploadPhoto(key, fileObj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public ResponseEntity<Room> save(@Valid @ModelAttribute Room room,
                                     BindingResult bindingResult,
                                     @RequestParam(name = "facilities", required = false) List<String> facilityIds,
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
        if (room.getRoomName().trim().isEmpty() || room.getNote().trim().isEmpty()) {
            return new ResponseEntity("Không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (roomService.existsByRoomCode(room.getRoomCode())) {
            return new ResponseEntity("Mã phòng đã tồn tại.", HttpStatus.BAD_REQUEST);
        }
        if (roomService.existsByRoomName(room.getRoomName())) {
            return new ResponseEntity("Tên phòng đã tồn tại.", HttpStatus.BAD_REQUEST);
        }

        try {

            String ma = "P" + (roomService.getList().size() + 1);
            room.setRoomCode(ma);
            room.setCreateAt(new Date());
            room.setUpdateAt(new Date());
            room.setStatus(Constant.ROOM.EMPTY);
            roomService.add(room);

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

//                listURL.add(imageUrl);
                Photo photo = new Photo();
                photo.setUrl(imageUrl);
                photo.setRoom(room);
                photo.setCreateAt(new Date());
                photo.setUpdateAt(new Date());
                photo.setStatus(Constant.COMMON_STATUS.ACTIVE);
                photoService.add(photo);
            }
            if (facilityIds != null) {
                for (String facilityId : facilityIds) {
                    Facility facility = facilityService.findById(facilityId);
                    if (facility != null) {
                        RoomFacility roomFacility = new RoomFacility();
                        roomFacility.setRoom(room);
                        roomFacility.setFacility(facility);
                        roomFacility.setCreateAt(new Date());
                        roomFacility.setUpdateAt(new Date());
                        roomFacility.setStatus(Constant.COMMON_STATUS.ACTIVE);
                        roomFacilityService.save(roomFacility);
                    }
                }
            }
            System.out.println("Thêm thành công");
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

    @GetMapping("/detail/{id}")
    public ResponseEntity<Room> detail(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Room> update(@PathVariable("id") String id, @Valid @ModelAttribute Room room,
                                       BindingResult bindingResult,
                                       @RequestParam(name = "facilities", required = false) List<String> facilityIds,
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
        if (room.getRoomCode().trim().isEmpty() || room.getRoomName().trim().isEmpty()
                || room.getNote().trim().isEmpty()) {
            return new ResponseEntity("Không được để trống", HttpStatus.BAD_REQUEST);
        }

        try {
//            Room existingRoom = roomService.getRoomById(id);

//            if (existingRoom == null) {
//                return new ResponseEntity("Room not found", HttpStatus.NOT_FOUND);
//            }
            room.setId(id);
            room.setUpdateAt(new Date());
            room.setStatus(Constant.ROOM.EMPTY);
            roomService.add(room);

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
                    photo.setRoom(room);
                    photo.setCreateAt(new Date());
                    photo.setUpdateAt(new Date());
                    photo.setStatus(Constant.COMMON_STATUS.ACTIVE);
                    photoService.add(photo);

                }
                System.out.println("Them Thanh cong ");
            }

            // Add new room facilities
            if (facilityIds != null) {
                for (String facilityId : facilityIds) {
                    Facility facility = facilityService.findById(facilityId);
                    if (facility != null) {
                        RoomFacility roomFacility = new RoomFacility();
                        roomFacility.setRoom(room);
                        roomFacility.setFacility(facility);
                        roomFacility.setCreateAt(new Date());
                        roomFacility.setUpdateAt(new Date());
                        roomFacility.setStatus(Constant.COMMON_STATUS.ACTIVE);
                        roomFacilityService.save(roomFacility);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        // Check phòng đang nằm trong hóa đơn
        List<Order> list = orderRepository.getRoomInOrder(id);
        if (list.size() != 0) {
            return new ResponseEntity<String>("Không thể xóa phòng vì phòng đang nằm trong hóa đơn", HttpStatus.BAD_REQUEST );
        }
        room.setStatus(Constant.ROOM.UNACTIVE);
        roomService.add(room);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

    @DeleteMapping("/change-status/{id}")
    public ResponseEntity<String> changeStatus(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        room.setStatus(Constant.ROOM.EMPTY);
        roomService.add(room);
        return new ResponseEntity<String>("Success " + id + " successfully", HttpStatus.OK);
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<List<PhotoDTO>> getRoomImages(@PathVariable("id") String id) {
        List<PhotoDTO> photoDTOs = new ArrayList<>();
        List<Photo> roomPhotos = photoService.getPhotoByRoomId(id);

        for (Photo photo : roomPhotos) {
            PhotoDTO photoDTO = new PhotoDTO();
            System.out.println("Id: " + photo.getId());
            photoDTO.setId(photo.getId());
            photoDTO.setUrl(photo.getUrl());
            photoDTOs.add(photoDTO);
        }
        return new ResponseEntity<>(photoDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/delete-photo/{id}")
    public ResponseEntity<String> deletePhoto(@PathVariable("id") String id) {
        System.out.println("Delete url: " + id);
        Photo photo = photoService.getPhotoById(id);

        if (photo != null) {
            photoService.deletePhoto(photo);
            return new ResponseEntity<String>("Photo deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Error deleting the photo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/facilities/{id}")
    public ResponseEntity<List<RoomFacility>> getFacilities(@PathVariable("id") String id) {
        List<RoomFacility> facilityList = roomFacilityService.getRoomFacilitiesByRoomId(id);
        return new ResponseEntity<>(facilityList, HttpStatus.OK);
    }

}

