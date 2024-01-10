package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.*;
import com.example.demo.entity.BlogComment;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Deposit;
import com.example.demo.entity.Facility;
import com.example.demo.entity.Room;
import com.example.demo.entity.Service;
import com.example.demo.entity.TypeRoom;
import com.example.demo.service.*;
import com.example.demo.util.DataUtil;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/home")
public class HomeController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private DepositService depositService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private TypeRoomService typeRoomService;
    @Autowired
    private ComboService comboService;
    @Autowired
    private HomeService homeService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogCommentService blogCommentService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private FavouriteService favouriteService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private BookingService bookingService;

    @GetMapping("/room/loadAndSearch")
    public Page<Room> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                    @RequestParam(name = "floorId", defaultValue = "") String floorId,
                                    @RequestParam(name = "typeRoomId", defaultValue = "") String typeRoomId,
                                    @RequestParam(name = "id", defaultValue = "") String id,
                                    @RequestParam(name = "current_page", defaultValue = "0") int current_page
    ) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.loadAndSearchForHome(key, key, floorId, typeRoomId, id, pageable);
    }

    @GetMapping("/room/loadAndSearch1")
    public Page<Room> loadAndSearch1(@RequestParam(name = "key", defaultValue = "") String key,
                                     @RequestParam(name = "floorId", defaultValue = "") String floorId,
                                     @RequestParam(name = "typeRoomId", defaultValue = "") String typeRoomId,
                                     @RequestParam(name = "status", defaultValue = "") Integer status,
                                     @RequestParam(name = "current_page", defaultValue = "0") int current_page
    ) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.loadAndSearch(key, key, floorId, typeRoomId, status, pageable);
    }

    @GetMapping("/room/load")
    public Page<Room> loadRoom(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 6);
        return roomService.getAllByStatus(Constant.COMMON_STATUS.ACTIVE, pageable);
    }

    @GetMapping("/room/loadByBook")
    public Page<Room> getRoomByVBooking(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.getAllByStatus(Constant.COMMON_STATUS.ACTIVE, pageable);
    }

    @GetMapping("/room/detail/{id}")
    public ResponseEntity<Room> detail(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    // Deposit
    @GetMapping("/deposit/getByCode")
    public ResponseEntity<Deposit> getByCode(@RequestParam("code") String code) {

        if (depositService.getByCode(code) != null) {
            Deposit deposit = depositService.getByCode(code);

            return new ResponseEntity<Deposit>(deposit, HttpStatus.OK);
        } else {
            return new ResponseEntity("Khong tim thay", HttpStatus.NOT_FOUND);
        }
    }

    // Service
    @GetMapping("/service/getAll")
    public List<Service> getAll() {
        return serviceService.getAll();
    }

    // Customer
    @GetMapping("/customer/{code}")
    public ResponseEntity<Customer> findByCode(@PathVariable("code") String code) {
        try {
            return new ResponseEntity<Customer>(customerService.findCustomerByCode(code), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("Khong tim thay " + code, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Customer> detailCustomer(@PathVariable("id") String id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PostMapping("/customer/save")
    public ResponseEntity<Customer> add(@RequestBody Customer customer,
                                        @Valid BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }

        if (customer.getFullname().isBlank()) {
            return new ResponseEntity("Full name không được bỏ trống", HttpStatus.BAD_REQUEST);
        }

        if (customer.getEmail().isBlank()) {
            return new ResponseEntity("Email không được để trống!", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$")) {
            return new ResponseEntity("Email không đúng định dạng!", HttpStatus.BAD_REQUEST);
        }
        if (customerService.existsByEmail(customer.getEmail())) {
            return new ResponseEntity("Email đã tồn tại, Vui lòng nhập E-mail mới!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getPhoneNumber().isBlank()) {
            return new ResponseEntity("Số điện thoại không được bỏ trống", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getPhoneNumber().matches("^(\\+84|0)[35789][0-9]{8}$")) {
            return new ResponseEntity("Số điện thoại không đúng định dạng!!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getCitizenId().isBlank()) {
            return new ResponseEntity("Căn cước công dân không được để trống!!", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getCitizenId().matches("\\d{12}")) {
            return new ResponseEntity("Căn cước công dân không đúng định dạng!!", HttpStatus.BAD_REQUEST);
        }
        if (customerService.existsByCitizenId(customer.getCitizenId())) {
            return new ResponseEntity("Căn cước công dân đã tồn tại!!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getPassword().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Mật khẩu", HttpStatus.BAD_REQUEST);
        }
//        if (customer.getPassword().length() < 5 || customer.getPassword().length() > 20) {
//            return new ResponseEntity("Mật khẩu phải từ 5-20 kí tự.", HttpStatus.BAD_REQUEST);
//        }

        customer.setCustomerCode(customerService.generateCustomerCode());
        customer.setCreateAt(new Date());
        customer.setUpdateAt(new Date());
        customer.setStatus(1);

        customerService.add(customer);
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> update(
            @PathVariable("id") String id,
            @Valid @RequestBody Customer customer,
            BindingResult result) {
        customer.setId(id);
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }

        if (customer.getFullname().isBlank()) {
            return new ResponseEntity("Full name không được bỏ trống", HttpStatus.BAD_REQUEST);
        }

        if (customer.getGender() == null) {
            return new ResponseEntity("Giới tính không được bỏ trống", HttpStatus.BAD_REQUEST);

        }

        if (customer.getBirthday() == null) {
            return new ResponseEntity("Ngày sinh không được để trống!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getEmail().isBlank()) {
            return new ResponseEntity("Email không được bỏ trống", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$")) {
            return new ResponseEntity("Email không đúng định dạng!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getPhoneNumber().isBlank()) {
            return new ResponseEntity("Số điện thoại không được bỏ trống", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getPhoneNumber().matches("^(\\+84|0)[35789][0-9]{8}$")) {
            return new ResponseEntity("Số điện thoại không đúng định dạng!!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getCitizenId().isBlank()) {
            return new ResponseEntity("Căn cước công dân không được để trống!!", HttpStatus.BAD_REQUEST);
        }
        if (!customer.getCitizenId().matches("\\d{12}")) {
            return new ResponseEntity("Căn cước công dân không đúng định dạng!!", HttpStatus.BAD_REQUEST);
        }

        if (customer.getPassword().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Mật khẩu", HttpStatus.BAD_REQUEST);
        }
//        if (customer.getPassword().length() < 5 || customer.getPassword().length() > 20) {
//            return new ResponseEntity("Mật khẩu phải từ 5-20 kí tự.", HttpStatus.BAD_REQUEST);
//        }

        if (customer.getProvinces().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Tỉnh.", HttpStatus.BAD_REQUEST);
        }
        if (customer.getDistricts().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Huyện.", HttpStatus.BAD_REQUEST);
        }
        if (customer.getWards().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Xã.", HttpStatus.BAD_REQUEST);
        }

        customer.setUpdateAt(new Date());
        customerService.add(customer);
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    @PutMapping("/changePassWord/{id}")
    public ResponseEntity<Customer> changePassWord(
            @PathVariable("id") String id,
            @RequestBody ChangePasswordData changePasswordData,
            @Valid BindingResult result
    ) {

        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }

        String newPassword = changePasswordData.getPassword();
        Customer customer = customerService.findById(id);

        if (customer.getPassword().isBlank()) {
            return new ResponseEntity("Không được bỏ trống Mật khẩu", HttpStatus.BAD_REQUEST);
        }
        if (newPassword.length() < 5 || newPassword.length() > 20) {
            return new ResponseEntity("Mật khẩu phải từ 5-20 kí tự.", HttpStatus.BAD_REQUEST);
        }
        customer.setPassword(newPassword);
        customer.setUpdateAt(new Date());
        customerService.add(customer);
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    // Filter
    @GetMapping("/get-room-filter")
    public List<Room> getRoomsByFilters(
            @RequestParam(name = "roomName", defaultValue = "") String roomName,
            @RequestParam(name = "typeRoomCode", defaultValue = "") String typeRoomCode,
            @RequestParam(name = "startPrice", defaultValue = "0") BigDecimal startPrice,
            @RequestParam(name = "endPrice", defaultValue = "20000000000000") BigDecimal endPrice,
            @RequestParam(name = "capacity", defaultValue = "1") Integer capacity,
            @RequestParam(name = "dayStart", defaultValue = "") Date dayStart,
            @RequestParam(name = "dayEnd", defaultValue = "") Date dayEnd
    ) {
        List<Room> roomList = roomService.findRoomsByFilters(
                roomName,
                typeRoomCode,
                startPrice,
                endPrice,
                capacity,
                dayStart,
                dayEnd
        );
        return roomList;
    }

    @GetMapping("/type-room/getList")
    public List<TypeRoom> getList() {
        return typeRoomService.getList();
    }


    @PostMapping("/room/search")
    public Page<RoomResponeDTO> testSearch(@RequestBody RoomRequestDTO roomRequestDTO,
                                           @RequestParam(name = "current_page", defaultValue = "0") int current_page,
                                           @RequestParam(name = "total_page", defaultValue = "1000") int total_page) {
        Pageable pageable = PageRequest.of(current_page, total_page);
        return roomService.search(roomRequestDTO, pageable);
    }

    @GetMapping("/room/top")
    public Page<RoomResponeDTO> testSearch(@RequestParam(name = "current_page", defaultValue = "0") int current_page,
                                           @RequestParam(name = "total_page", defaultValue = "1000") int total_page) {
        Pageable pageable = PageRequest.of(current_page, total_page);
        return roomService.topBook(pageable);
    }

    @GetMapping("/combo/getall")
    public List<ComboDTO> getAllCombo() {
        return comboService.getAll();
    }

    @GetMapping("/cart/{customId}/{odStt}")
    public List<CartDTO> getCart(@PathVariable("customId") String customId, @PathVariable("odStt") Integer odStt) {
        return roomService.getCart(customId, odStt);
    }

    @PostMapping("/login")
    public ResponseEntity<Customer> login(@RequestBody CustomerLoginDTO customerLoginDTO) {
//        return new ResponseEntity<>(customerService.login(customerLoginDTO), HttpStatus.OK);
        if (customerService.login(customerLoginDTO) == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(customerService.login(customerLoginDTO), HttpStatus.OK);
    }

    @PostMapping("/order/cancel/{code}/{oddStt}")
    public ResponseEntity<Message> cancelOrder(@PathVariable("code") String code,
                                               @PathVariable("oddStt") Integer oddStt,
                                               @RequestParam(name = "refuseReason", defaultValue = "") String refuseReason) {
        return new ResponseEntity<>(homeService.cancelOrder(code, oddStt, refuseReason), HttpStatus.OK);
    }


    @GetMapping("/load/blog")
    public Page<BlogDTO> load(@RequestParam(name = "current_page", defaultValue = "0") int current_page,
                              @RequestParam(name = "total", defaultValue = "6") int total) {
        Pageable pageable = PageRequest.of(current_page, total);
        return blogService.getPaginate(pageable);
    }

    //?blogId=${id}&&customerId=${customer.id}
    @GetMapping("/view")
    public Integer likeBlog(@RequestParam("blogId") String blogId) {
        return blogService.countView(blogId);
    }

    @GetMapping("/blog/comment")
    public List<BlogCommentDTO> getComment(@RequestParam("blogId") String blogId,
                                           @RequestParam(name = "currentPage", defaultValue = "0") Integer currentPage) {
        return getListComment(currentPage, blogId);
    }

    private List<BlogCommentDTO> getListComment(Integer currentPage, String blogId) {
        Pageable pageable = PageRequest.of(currentPage, 15000000);
        Page<BlogComment> page = blogCommentService.getPaginate(blogId, pageable);
        List<BlogComment> list = page.getContent();
        List<BlogCommentDTO> commentDTOList = new ArrayList<>();
        for (BlogComment cm : list) {
            commentDTOList.add(fromEntity(cm));
        }
        return commentDTOList;
    }

    @GetMapping("/blog/comment/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") String id) {
        blogCommentService.deleteComment(id);
        return ResponseEntity.ok(null);
    }


    private static BlogCommentDTO fromEntity(BlogComment entity) {
        BlogCommentDTO dto = new BlogCommentDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setContent(entity.getContent());
        dto.setIdBlog(entity.getIdBlog());
        dto.setCreatedAt(entity.getCreateAt());
        return dto;
    }

    @PostMapping("/date-booked")
    public List<List<String>> getDateBooked(@RequestBody List<RoomId> dates) {
        List<List<String>> listFinal = new ArrayList<>();
        List<String> roomIds = dates.stream().map(item -> item.getId()).collect(Collectors.toList());
        listFinal.add(orderDetailService.getOrderByRoomIds(roomIds));
        List<String> startDate = orderDetailService.getStartDate(roomIds);
        List<String> endDate = orderDetailService.getEndDate(roomIds);
        listFinal.add(startDate);
        listFinal.add(endDate);
        return listFinal;
    }


    @GetMapping("/check-love")
    public ResponseEntity<Boolean> checkLove(@RequestParam("idCustom") String idCustom,
                                             @RequestParam("idRoom") String idRoom) {
        return new ResponseEntity<>(favouriteService.isLove(idCustom, idRoom), HttpStatus.OK);
    }

    @GetMapping("/set-love")
    public ResponseEntity<Boolean> setLove(@RequestParam("idCustom") String idCustom,
                                           @RequestParam("idRoom") String idRoom) {
        return new ResponseEntity<>(favouriteService.setLove(idCustom, idRoom), HttpStatus.OK);
    }

    @GetMapping("/load/favourite/{customerId}")
    public ResponseEntity<Page<FavouriteRoomDTO>> getFavouriteRoomsByCustomerId(@PathVariable String customerId,
                                                                                @RequestParam(name = "current_page", defaultValue = "0") Integer current_page) {
        Pageable pageable = PageRequest.of(current_page, 6);
        Page<FavouriteRoomDTO> favouriteRooms = favouriteService.getFavouriteRoomsByCustomerId(customerId, pageable);
        return ResponseEntity.ok(favouriteRooms);
    }

    @GetMapping("/load/facility")
    public ResponseEntity<List<Facility>> getFacility() {
        return ResponseEntity.ok(facilityService.getAll());
    }

    @PostMapping("/search/facility")
    public ResponseEntity<Page<RoomCardDTO>> searchRoom(@RequestBody FacilityRequestDTO facilityRequestDTO) {
        System.out.println(facilityRequestDTO);
        Pageable pageable = PageRequest.of(facilityRequestDTO.getCurrentPage(), 6);
        return new ResponseEntity<>(roomService.searchRoom(facilityRequestDTO, pageable), HttpStatus.OK);
    }


    @GetMapping("/type-room/search")
    public Page<TypeRoom> searchTypeRoom(@RequestParam(name = "key", defaultValue = "") String key,
                                         @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        if (StringUtils.isBlank(key) || "".equals(key)) {
            return typeRoomService.getAll(pageable);
        }

        return typeRoomService.findByCodeOrName(key, pageable);
    }

    @GetMapping("/type-room/detail/{id}")
    public ResponseEntity<TypeRoom> detailTypeRoom(@PathVariable("id") String id) {
        TypeRoom typeRoom = typeRoomService.getTypeRoomById(id);
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @GetMapping("/type-room/getByName")
    public ResponseEntity<TypeRoom> findByName(@RequestParam("name") String name) {
        TypeRoom typeRoom = typeRoomService.findByName(name);
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @PostMapping("/booking/check-room")
    public Integer countRoomCanBeBook(@RequestBody Map<String, Object> requestBody) {
        String checkInStr = (String) requestBody.get("checkIn");
        String checkOutStr = (String) requestBody.get("checkOut");
        LocalDate checkIn = DataUtil.convertStringToLocalDate(checkInStr);
        LocalDate checkOut = DataUtil.convertStringToLocalDate(checkOutStr);
        Date checkInDateConfig = DataUtil.convertLocalDateToDateWithTime(checkIn, 14);
        Date checkOutDateConfig = DataUtil.convertLocalDateToDateWithTime(checkOut, 12);
        String typeRoom = (String) requestBody.get("typeRoomChose");
        return typeRoomService.countRoomCanBeBook(typeRoom, checkInDateConfig, checkOutDateConfig);
    }

    @GetMapping("/booking/get-by-status/{status}/{idCuss}")
    public List<Booking> getAllByStatus(@PathVariable("status") Integer status, @PathVariable("idCuss") String idCuss) {
        return bookingService.getAllByStatus(status, idCuss);
    }

    @GetMapping("/booking/cancel/{id}")
    public ResponseEntity<Void> cancel(@PathVariable("id") String id) {
        if (bookingService.cancel(id)) return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
