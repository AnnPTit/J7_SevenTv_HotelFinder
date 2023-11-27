package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.BlogCommentDTO;
import com.example.demo.dto.LikeDTO;
import com.example.demo.dto.PayloadObject;
import com.example.demo.model.Mail;
import com.example.demo.service.MailService;
import com.example.demo.dto.RoomData;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.example.demo.util.DataUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
        // Todo : Số khách 100
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PayloadObject payload = objectMapper.readValue(message, PayloadObject.class);
            System.out.println(payload);
            // Kiểm tra ngày check in có phải ngày hôm nay
            // Kiểm tra ngày đặt đã trùng
            List<String> idsRoom = payload.getRooms().stream()
                    .map(RoomData::getId)
                    .collect(Collectors.toList());
            Date today = new Date();
            String dateString = payload.getDayStart().toString().substring(0, 11);
            String dateStringEnd = payload.getDayEnd().toString().substring(0, 11);
            String todayString = today.toString().substring(0, 11);
            if (dateString.equals(todayString)) {
                return new Response("Ngày checkIn phải lớn hơn ngày hôm nay !",
                        Constant.COMMON_STATUS.ACTIVE, idsRoom);
            }
            if (dateString.equals(dateStringEnd)) {
                return new Response("Số ngày đặt phải lớn hơn 1 ",
                        Constant.COMMON_STATUS.ACTIVE, idsRoom);
            }
            // Kiểm tra ngày đặt nằm trong khoảng 1 tháng tới
            if (!DataUtil.isInOneMonth(payload.getDayStart()) && !DataUtil.isInOneMonth(payload.getDayEnd())) {
                return new Response("Vui lòng đặt phòng trong vòng 30 ngày !",
                        Constant.COMMON_STATUS.ACTIVE, idsRoom);
            }
            List<String> orderDetailIds = orderDetailService.checkRoomIsBooked(DataUtil.dateToStringSql(payload.getDayStart()),
                    DataUtil.dateToStringSql(payload.getDayEnd()), idsRoom);
            System.out.println(orderDetailIds.toString());
            if (!orderDetailIds.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String startDate = sdf.format(payload.getDayStart());
                String endDate = sdf.format(payload.getDayEnd());
                return new Response("Phòng đã được đặt trong khoảng :  "
                        + startDate + "   đến ngày :    " + endDate + "  !" + ". Vui lòng chọn ngày khác !",
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
                newCustomer.setCitizenId(Constant.citizenId);
                newCustomer.setBirthday(birthDate);
                newCustomer.setDistricts("N/A");
                newCustomer.setStatus(Constant.COMMON_STATUS.ACTIVE);
                newCustomer.setCreateAt(new Date());
                newCustomer.setEmail(payload.getUser().getEmail());
                newCustomer.setFullname(payload.getUser().getHoVaTen());
                newCustomer.setPassword(customerCode + "12345");
                newCustomer.setPhoneNumber(payload.getUser().getSoDienThoai());
                customerService.add(newCustomer);
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
                orderDetail.setCustomerQuantity(roomData.getGuestCount());
                orderDetail.setOrderDetailCode("HDCT" + randomNumber);
                orderDetail.setRoomPrice(payload.getTotalPriceRoom());
                if (roomData.getGuestCount() > room.getTypeRoom().getCapacity()) {
                    return new Response("Số khách vượt quá sức chứa  !",
                            Constant.COMMON_STATUS.ACTIVE, idsRoom);
                }
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
                    "Tên khách hàng : " + customer.getFullname() + "\n" +
                    "Số điện thoại : " + customer.getPhoneNumber() + "\n" +
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
            return new Response("Đặt phòng thành công !", Constant.COMMON_STATUS.ACTIVE, idsRoom);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    public List<BlogCommentDTO> comment(String message) throws JsonProcessingException {
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
