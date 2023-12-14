package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.BlogCommentDTO;
import com.example.demo.dto.LikeDTO;
import com.example.demo.dto.PayloadObject;
import com.example.demo.dto.RoomData;
import com.example.demo.entity.*;
import com.example.demo.model.Mail;
import com.example.demo.service.*;
import com.example.demo.util.DataUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final CustomerService customerService;

    private final RoomService roomService;

    private final OrderService orderService;

    private final OrderTimelineService orderTimelineService;

    private final OrderDetailService orderDetailService;

    private final InformationCustomerService informationCustomerService;

    private final AccountService accountService;

    private final MailService mailService;

    private final DepositService depositService;

    private final BlogService blogService;

    private final BlogCommentService blogCommentService;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class Response {
        private String message;
        private int status;
        private List<String> ids;
    }

    @MessageMapping("/products")
    @SendTo("/topic/product")
    public Response broadcastNews(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PayloadObject payload = objectMapper.readValue(message, PayloadObject.class);
            System.out.println(payload);
            List<String> idsRoom = payload.getRooms().stream()
                    .map(RoomData::getId)
                    .collect(Collectors.toList());
            // Kiểm tra ngày check in có phải ngày hôm nay
            // Kiểm tra ngày đặt đã trùng
            Date today = new Date();
            if ((payload.getDayStart()) == null) {
                return new Response(payload.getKeyToken() + "Vui lòng nhập lại ! [",
                        Constant.COMMON_STATUS.ACTIVE, idsRoom);
            }
            String dateString = payload.getDayStart().toString().substring(0, 11);
            String dateStringEnd = payload.getDayEnd().toString().substring(0, 11);
            String todayString = today.toString().substring(0, 11);
            // Thêm validate ngày đặt phải lớn hơn ngày disable gần nhất 2 ngày
            Date endCompare = DataUtil.setFixedTime(payload.getDayStart(), 14, 0, 0);
            // lấy ra ngày bị disable gần nhất nhỏ hơn ngày đặt
            List<RoomData> roomDataList = payload.getRooms();
            List<String> roomIds = roomDataList.stream().map(item -> item.getId()).collect(Collectors.toList());
            List<String> dates1 = orderDetailService.getOrderByRoomIds(roomIds);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startCompare = getNearestDate(endCompare, dates1, dateFormat);
            if (!isEndDateValid(startCompare, endCompare)) {
                return new Response(payload.getKeyToken() + "Bạn không được để một ngày bị trống ! [",
                        Constant.COMMON_STATUS.ACTIVE, idsRoom);
            }
            if (dateString.equals(todayString)) {
                return new Response(payload.getKeyToken() + "Ngày checkIn phải lớn hơn ngày hôm nay ! [",
                        Constant.COMMON_STATUS.ACTIVE, idsRoom);
            }
            if (dateString.equals(dateStringEnd)) {
                return new Response(payload.getKeyToken() + "Số ngày đặt phải lớn hơn 1 [",
                        Constant.COMMON_STATUS.ACTIVE, idsRoom);
            }
            // Kiểm tra ngày đặt nằm trong khoảng 1 tháng tới
            if (!DataUtil.isInOneMonth(payload.getDayStart()) && !DataUtil.isInOneMonth(payload.getDayEnd())) {
                return new Response(payload.getKeyToken() + "Vui lòng đặt phòng trong vòng 30 ngày ! [",
                        Constant.COMMON_STATUS.ACTIVE, idsRoom);
            }
            List<String> orderDetailIds = orderDetailService.checkRoomIsBooked(DataUtil.dateToStringSql(payload.getDayStart()),
                    DataUtil.dateToStringSql(payload.getDayEnd()), idsRoom);
            System.out.println(orderDetailIds.toString());
            if (!orderDetailIds.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String startDate = sdf.format(payload.getDayStart());
                String endDate = sdf.format(payload.getDayEnd());
                return new Response(payload.getKeyToken() + "Phòng đã được đặt trong khoảng :  "
                        + startDate + "   đến ngày :    " + endDate + "  !" + ". Vui lòng chọn ngày khác ! [",
                        Constant.COMMON_STATUS.ACTIVE, idsRoom);
            }
            if (validateDetail(payload, idsRoom)) {
                return new Response(payload.getKeyToken() + "Số khách vượt quá sức chứa  ! [",
                        Constant.COMMON_STATUS.ACTIVE, idsRoom);
            }
            Random random = new Random();
            int randomNumber = random.nextInt(1000);
            Customer customer = customerService.findCustomerByEmail(payload.getUser().getEmail()).orElse(null);
            Customer newCustomer = new Customer();
            boolean isNewCustom = false;
            if (customer == null) {
                isNewCustom = true;
                Date currentDate = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.YEAR, -18);
                Date birthDate = calendar.getTime();
                String customerCode = "KH" + randomNumber;
                newCustomer.setCustomerCode(customerCode);
                newCustomer.setUsername(customerCode);
                newCustomer.setCitizenId(null);
                newCustomer.setBirthday(birthDate);
                newCustomer.setDistricts("N/A");
                newCustomer.setStatus(Constant.COMMON_STATUS.ACTIVE);
                newCustomer.setCreateAt(new Date());
                newCustomer.setEmail(payload.getUser().getEmail());
                newCustomer.setFullname(payload.getUser().getHoVaTen());
                newCustomer.setPassword(customerCode + "12345");
                newCustomer.setPhoneNumber(payload.getUser().getSoDienThoai());
                customer = customerService.add(newCustomer);
            }
            // B2 : Lấy account
            Account account = accountService.getAccountByCode();
            // B3 : Tạo hóa đơn
            Order order = new Order();
            Customer customerForOrder = customerService.findCustomerByEmail(payload.getUser().getEmail()).orElse(null);
            if (!Objects.isNull(customerForOrder)) {
                order.setCustomer(customerForOrder);
            } else {
                order.setCustomer(newCustomer);
            }
            String orderCode = "HD" + randomNumber;
            order.setAccount(account);
            order.setOrderCode(orderCode);
            order.setTypeOfOrder(false);
            order.setDeposit(payload.getDeposit());
            order.setVat((payload.getTotalPriceRoom().multiply(new BigDecimal(depositService.getByCode("VAT").getPileValue()))).divide(new BigDecimal(100)));
            order.setBookingDateStart(DataUtil.setFixedTime(payload.getDayStart(), 14, 0, 0));
            order.setBookingDateEnd(DataUtil.setFixedTime(payload.getDayEnd(), 12, 0, 0));
            order.setTotalMoney(payload.getTotalPriceRoom());
            order.setCreateAt(new Date());
            order.setUpdateAt(new Date());
            order.setNote(payload.getNote());
            order.setStatus(Constant.ORDER_STATUS.WAIT_CONFIRM);
            orderService.add(order);
            // B4 tạo Order timeline
            OrderTimeline orderTimeline = new OrderTimeline();
            orderTimeline.setOrder(order);
            orderTimeline.setAccount(account);
            orderTimeline.setType(Constant.ORDER_TIMELINE.WAIT_CONFIRM);
            orderTimeline.setNote("Khách hàng tạo hóa đơn" + payload.getUser().getEmail());
            orderTimeline.setCreateAt(new Date());
            orderTimelineService.add(orderTimeline);
            // B5 : Tạo hóa đơn chi tiết
            List<OrderDetail> orderDetailList = new ArrayList<>();
            for (RoomData roomData : payload.getRooms()
            ) {
                Room room = roomService.getRoomById(roomData.getId());
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setRoom(room);
                orderDetail.setOrder(order);
                orderDetail.setOrderDetailCode("HDCT" + randomNumber);
                // Tính toán ngày và nhân với đơn giá
                Date bookingStart = payload.getDayStart();
                Date bookingEnd = payload.getDayEnd();
                Instant startInstant = bookingStart.toInstant();
                Instant endInstant = bookingEnd.toInstant();
                // Calculate the duration between the two instants
                Duration duration = Duration.between(startInstant, endInstant);
                // Get the number of days
                long days = duration.toDays();
                BigDecimal pricePerDay = room.getTypeRoom().getPricePerDay();
                BigDecimal totalCost = pricePerDay.multiply(BigDecimal.valueOf(days));
                orderDetail.setRoomPrice(totalCost);
                orderDetail.setCustomerQuantity(roomData.getGuestCount());
                orderDetail.setCreateAt(new Date());
                orderDetail.setUpdateAt(new Date());
                orderDetail.setCheckInDatetime(payload.getDayStart());
                orderDetail.setCheckOutDatetime(payload.getDayEnd());
                orderDetail.setStatus(Constant.ORDER_STATUS.WAIT_CONFIRM);
                roomService.add(room);
                // Thêm hóa đơn chi tiết
                orderDetailList.add(orderDetail);
            }
            orderDetailService.addAll(orderDetailList);
            InformationCustomer informationCustomer;
            if (!Objects.isNull(customerForOrder)) {
                informationCustomer = DataUtil.convertCustomerToInformationCustomer(customerForOrder);
            } else {
                informationCustomer = DataUtil.convertCustomerToInformationCustomer(newCustomer);
            }
            informationCustomer.setOrderDetail(orderDetailList.get(0));
            informationCustomerService.add(informationCustomer);

            // gửi mail
            Mail mail = new Mail();
            mail.setMailFrom("nguyenvantundz2003@gmail.com");
            mail.setMailTo(payload.getUser().getEmail());
            String subject = "Đặt phòng thành công ! ";
            String content = "Chúc mừng bạn đặt phòng thành công ! \n" +
                    "Thông tin đơn hàng của bạn : \n" +
                    "Mã hóa đơn : " + orderCode + "\n" +
                    "Tên khách hàng : " + payload.getUser().getHoVaTen() + "\n" +
                    "Số điện thoại : " + payload.getUser().getSoDienThoai() + "\n" +
                    "Ngày đặt : " + DataUtil.convertDateToString(order.getCreateAt()) + "\n" +
                    "Ngày CheckIn : " + DataUtil.convertDateToString(order.getBookingDateStart()) + "\n" +
                    "Ngày CheckOut : " + DataUtil.convertDateToString(order.getBookingDateEnd()) + "\n" +
                    "Tổng tiền phòng tạm tính : " + DataUtil.formatMoney(order.getTotalMoney()) + "\n" +
                    "Số tiền phải cọc : " + DataUtil.formatMoney(order.getDeposit()) + "\n" +
                    "Chi tiết : ";
            for (OrderDetail orderDetail : orderDetailList) {
                content = content + "\n" +
                        "- Phòng : " + orderDetail.getRoom().getRoomName() + " \nSố khách : " + orderDetail.getCustomerQuantity() + "\n";
            }

            if (isNewCustom) {
                content = content + "\n Bạn vui lòng đăng nhập với thông tin sau để theo dõi đơn hàng : \n " +
                        "Tên đăng nhập : " + customer.getEmail() + "\n" +
                        "Mật khẩu : " + customer.getPassword() + "\n" +
                        "Tại đường dẫn sau : http://localhost:3000/sign-in";
            }
            content = content + "\n Chúc bạn có một trải nghiệm tuyệt vời ! \n";
            mail.setMailSubject(subject);
            mail.setMailContent(content);
            mailService.sendEmail(mail);
            // List ở giữa
            List<String> dates = orderDetailService.getOrderByRoomIds(roomIds);
            List<String> startDate = orderDetailService.getStartDate(roomIds);
            List<String> endDate = orderDetailService.getEndDate(roomIds);
            // List ngày bắt đầu
            return new Response(payload.getKeyToken() + "Đặt phòng thành công" + dates +startDate+endDate, Constant.COMMON_STATUS.ACTIVE, idsRoom);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isEndDateValid(Date startDate, Date endDate) {
        // Kiểm tra nếu endDate là null hoặc startDate là null
        if (startDate == null || endDate == null) {
            return false;
        }

        // Chuyển đổi Date thành LocalDate
        LocalDate localStartDate = startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate localEndDate = endDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        // Tính khoảng cách giữa endDate và startDate trong ngày
        long daysDifference = localEndDate.toEpochDay() - localStartDate.toEpochDay();

        // Kiểm tra nếu endDate lớn hơn startDate ít nhất 2 ngày
        if (daysDifference == 1) {
            return true;
        } else if (daysDifference > 2) {
            return true;
        }
        return false;
    }

    public static Date getNearestDate(Date start, List<String> dates, SimpleDateFormat dateFormat)
            throws ParseException {
        Date nearestDate = null;
        long minDifference = Long.MAX_VALUE;

        for (String dateString : dates) {
            Date currentDate = dateFormat.parse(dateString);

            // Đặt giờ và phút của currentDate thành 14:00:00
            currentDate.setHours(14);
            currentDate.setMinutes(0);
            currentDate.setSeconds(0);

            // Tính độ chênh lệch giữa start và currentDate
            long difference = Math.abs(currentDate.getTime() - start.getTime());

            // Nếu độ chênh lệch nhỏ hơn minDifference, cập nhật nearestDate và minDifference
            if (difference < minDifference) {
                minDifference = difference;
                nearestDate = currentDate;
            }
        }

        // Nếu không có ngày trong danh sách, hoặc tất cả các ngày đều lớn hơn start,
        // thì lấy mặc định là ngày hôm nay
        if (nearestDate == null || start.before(nearestDate)) {
            nearestDate = new Date();
            nearestDate.setHours(14);
            nearestDate.setMinutes(0);
            nearestDate.setSeconds(0);
        }

        return nearestDate;
    }

    private boolean validateDetail(PayloadObject payload, List<String> idsRoom) {

        for (RoomData roomData : payload.getRooms()
        ) {
            Room room = roomService.getRoomById(roomData.getId());
            if (roomData.getGuestCount() > room.getTypeRoom().getCapacity()) {
                return true;
            }
        }
        return false;
    }

    @MessageMapping("/likes")
    @SendTo("/topic/like")
    public Integer like(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Integer countLike = 0;
        try {
            LikeDTO payload = objectMapper.readValue(message, LikeDTO.class);
            // Them like
            countLike = blogService.countLike(payload.getBlogId());
            // Update like
            Blog blog = blogService.findOne(payload.getBlogId());
            if (payload.isIslike()) {
                blogService.like(payload.getBlogId(), payload.getCustomerId());
                blog.setCountLike(countLike + 1);
            } else {
                blogService.unLike(payload.getBlogId(), payload.getCustomerId());
                blog.setCountLike(countLike - 1);
            }
            blogService.save(blog);
            if (payload.isIslike()) {
                return countLike + 1;
            }
            return countLike - 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    @MessageMapping("/comments")
    @SendTo("/topic/comment")
    public List<BlogCommentDTO> comment(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<BlogCommentDTO> commentDTOList = new ArrayList<>();
            BlogCommentDTO payload = objectMapper.readValue(message, BlogCommentDTO.class);
            if (payload.getIsCreate()) {
                // Them comment ở đây
                BlogComment blogComment = new BlogComment();
                blogComment.setIdBlog(payload.getIdBlog());
                blogComment.setContent(payload.getContent());
                blogComment.setUsername(payload.getUsername());
                blogComment.setCreateAt(new Date());
                blogCommentService.save(blogComment);
                Pageable pageable = PageRequest.of(0, 15);
                Page<BlogComment> page = blogCommentService.getPaginate(payload.getIdBlog(), pageable);
                List<BlogComment> list = page.getContent();
                for (BlogComment cm : list) {
                    commentDTOList.add(fromEntity(cm));
                }

            }
            return commentDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

}
