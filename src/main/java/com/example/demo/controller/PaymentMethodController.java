package com.example.demo.controller;

import com.example.demo.config.MomoConfig;
import com.example.demo.config.VNPayConfig;
import com.example.demo.config.ZaloPayConfig;
import com.example.demo.constant.Constant;
import com.example.demo.entity.*;
import com.example.demo.model.Mail;
import com.example.demo.service.*;
import com.example.demo.util.BaseService;
import com.example.demo.util.DataUtil;
import com.example.demo.util.HMACUtil;
import com.example.demo.util.MomoEncoderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/payment-method")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private HistoryTransactionService historyTransactionService;
    @Autowired
    private OrderTimelineService orderTimelineService;
    @Autowired
    private DiscountProgramService discountProgramService;
    @Autowired
    private MailService mailService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private TypeRoomService typeRoomService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingHistoryTransactionService bookingHistoryTransactionService;
    @Autowired
    private BaseService baseService;


    @GetMapping("/loadAndSearch")
    public Page<PaymentMethod> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                             @RequestParam(name = "method", defaultValue = "") Boolean method,
                                             @RequestParam(name = "startDate", defaultValue = "") String startDateStr,
                                             @RequestParam(name = "endDate", defaultValue = "") String endDateStr,
                                             @RequestParam(name = "current_page", defaultValue = "0") int current_page) {

        Date startDate = null;
        Date endDate = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (!startDateStr.isEmpty()) {
                startDate = dateFormat.parse(startDateStr);
            }

            if (!endDateStr.isEmpty()) {
                endDate = dateFormat.parse(endDateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Pageable pageable = PageRequest.of(current_page, 5);
        return paymentMethodService.loadAndSearch(key, method, key, startDate, endDate, pageable);
    }

    @GetMapping("/load/{id}")
    public List<PaymentMethod> loadByOrderId(@PathVariable("id") String id) {
        return paymentMethodService.getAllByOrderId(id);
    }

    @PostMapping("/payment-vnpay/{id}")
    @ResponseBody
    public ResponseEntity<?> vnPayPost(HttpServletRequest req, @PathVariable("id") String id, @RequestBody Map<String, Object> requestBody) throws UnsupportedEncodingException {
        Order order = orderService.getOrderById(id);
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        long amount = ((Integer) requestBody.get("amount")).longValue() * 100;
        long discount = ((Integer) requestBody.get("discount")).longValue() * 100;
        String idDiscount = (String) requestBody.get("idDiscount");
        System.out.println(order.getId() + idDiscount);

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(req);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "");
        if (idDiscount != null) {
            vnp_Params.put("vnp_TxnRef", order.getId() + idDiscount);
        } else {
            vnp_Params.put("vnp_TxnRef", order.getId());
        }
        vnp_Params.put("vnp_OrderInfo", String.valueOf(discount));
        vnp_Params.put("vnp_OrderType", "billpayment1321");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = "" + vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        vnp_Params.put("finalUrl", paymentUrl);

        return ResponseEntity.ok(vnp_Params);
    }

    @PostMapping("/payment-vnpay")
    @ResponseBody
    public ResponseEntity<?> vnPayPostBook(HttpServletRequest req, @RequestBody Map<String, Object> requestBody) throws UnsupportedEncodingException {
//        Order order = orderService.getOrderById(id);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        // Lấy thông tin -> lưu vào bảng booking với trạng thái unactive
        // Thông tin khách hàng
        if (validate(requestBody) != null) {
            return new ResponseEntity<>(validate(requestBody), HttpStatus.BAD_REQUEST);
        }
        Customer customer = createCustomer(requestBody);
        Booking booking = createBooking(requestBody, customer);
        Long amount = booking.getTotalPrice().multiply(new BigDecimal(100)).longValue();


        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(req);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "");
        vnp_Params.put("vnp_TxnRef", "BKOL" + booking.getId());
        vnp_Params.put("vnp_OrderInfo", String.valueOf(booking.getTotalPrice()));
        vnp_Params.put("vnp_OrderType", "billpayment1321");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = "" + vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        vnp_Params.put("finalUrl", paymentUrl);
//        DataUtil.sendMailCommon("anptph27230@fpt.edu.vn","test",paymentUrl,mailService);

        return ResponseEntity.ok(vnp_Params);
    }

    private String validate(Map<String, Object> requestBody) {
        long amount = ((Integer) requestBody.get("amount")).longValue();
        long roomPrice = ((Integer) requestBody.get("roomPrice")).longValue();
        String checkInStr = (String) requestBody.get("checkIn");
        String checkOutStr = (String) requestBody.get("checkOut");
        LocalDate checkIn = DataUtil.convertStringToLocalDate(checkInStr);
        LocalDate checkOut = DataUtil.convertStringToLocalDate(checkOutStr);
        Date checkInDateConfig = DataUtil.convertLocalDateToDateWithTime(checkIn, 14);
        Date checkOutDateConfig = DataUtil.convertLocalDateToDateWithTime(checkOut, 12);
        Integer numberNight = Integer.valueOf(requestBody.get("numberNight").toString());
        Integer numberRoom = Integer.valueOf(requestBody.get("numberRoom").toString());
        Integer numberCustomer = Integer.valueOf(requestBody.get("numberCustomer").toString());
        Integer numberChildren = Integer.valueOf(requestBody.get("numberChildren").toString());
        String typeRoomChose = (String) requestBody.get("typeRoomChose");
        String note = (String) requestBody.get("note");
        String fullName = (String) requestBody.get("fullName");
        String phoneNumber = (String) requestBody.get("phoneNumber");
        String email = (String) requestBody.get("email");
        // History
        String accountNumber = (String) requestBody.get("accountNumber");
        String bankChose = requestBody.get("bankChose").toString();

        // validate số phòng
        Integer numberRoomCanBeBook = typeRoomService.countRoomCanBeBook(typeRoomChose, checkInDateConfig, checkOutDateConfig);
        if (numberRoom > numberRoomCanBeBook) {
            return "Số phòng còn trống trong khoảng " + checkInStr + " / " + checkOutStr + " không đủ đáp ứng ! \n vui lòng chọn loại phòng khác hoặc khoảng ngày khác !";
        }
        Date now = new Date();
        if (checkInDateConfig.before(now)) {
            return "Ngày check in phải lớn hơn ngày hôm nay";
        }

        if (!checkOutDateConfig.after(checkInDateConfig)) {
            return "Ngày check out phải lớn hơn ngày check in";
        }

        if (DataUtil.isNull(fullName)) {
            return "Không được bỏ trống Họ và tên";
        }
        if (DataUtil.isNull(phoneNumber)) {
            return "Không được bỏ trống Số điện thoại";
        }
        if (DataUtil.isNull(email)) {
            return "Không được bỏ trống Email";
        }
        if (DataUtil.isNull(accountNumber)) {
            return "Không được bỏ trống Số tài khoản ngân hàng";
        }

        if (!phoneNumber.matches("^(\\+84|0)[35789][0-9]{8}$")) {
            return "Số điện thoại không đúng định dạng";
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$")) {
            return "Email không đúng định dạng";
        }
        if (!accountNumber.matches("^[0-9]{5,20}$")) {
            return "Số tài khoản ngân hàng không đúng định dang";
        }

        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        if (!(checkIn.isAfter(currentDate) && checkIn.isBefore(currentDate.plusDays(30)))) {
            return "Ngày Check-in không được vượt quá 30 ngày.";
        }
        return null;
    }

    private Customer createCustomer(Map<String, Object> requestBody) {
        String fullName = (String) requestBody.get("fullName");
        String phoneNumber = (String) requestBody.get("phoneNumber");
        String email = (String) requestBody.get("email");
        // Thực hiện validate xem khách hàng đã có tài khoản chưa ?
        Customer customer = customerService.findCustomerByEmail(email).orElse(null);
        if (DataUtil.isNull(customer)) {
            // Nếu email chưa tồn tại thực hiện thêm tài khoản
            Customer customer1 = new Customer();
            Date currentDate = new Date();
            Random random = new Random();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.YEAR, -18);
            Date birthDate = calendar.getTime();
            String customerCode = "KH" + random.nextInt(1000);
            customer1.setCustomerCode(customerCode);
            customer1.setUsername(customerCode);
            customer1.setCitizenId(null);
            customer1.setBirthday(birthDate);
            customer1.setDistricts("N/A");
            customer1.setStatus(Constant.COMMON_STATUS.ACTIVE);
            customer1.setCreateAt(new Date());
            customer1.setEmail(email);
            customer1.setFullname(fullName);
            customer1.setPassword(customerCode + "12345");
            customer1.setPhoneNumber(phoneNumber);
            customer1 = customerService.add(customer1);
            return customer1;
        }
        return customer;

    }

    private Booking createBooking(Map<String, Object> requestBody, Customer customer) {

        String checkInStr = (String) requestBody.get("checkIn");
        String checkOutStr = (String) requestBody.get("checkOut");
        LocalDate checkIn = DataUtil.convertStringToLocalDate(checkInStr);
        LocalDate checkOut = DataUtil.convertStringToLocalDate(checkOutStr);
        Date checkInDateConfig = DataUtil.convertLocalDateToDateWithTime(checkIn, 14);
        Date checkOutDateConfig = DataUtil.convertLocalDateToDateWithTime(checkOut, 12);
        Integer numberNight = Integer.valueOf(requestBody.get("numberNight").toString());
        Integer numberRoom = Integer.valueOf(requestBody.get("numberRoom").toString());
        Integer numberCustomer = Integer.valueOf(requestBody.get("numberCustomer").toString());
        Integer numberChildren = Integer.valueOf(requestBody.get("numberChildren").toString());
        String typeRoomChose = (String) requestBody.get("typeRoomChose");
        String note = (String) requestBody.get("note");
        long amount = ((Integer) requestBody.get("amount")).longValue() * numberRoom;
        long roomPrice = ((Integer) requestBody.get("roomPrice")).longValue() * numberRoom;
        // History
        String accountNumber = (String) requestBody.get("accountNumber");
        String bankChose = requestBody.get("bankChose").toString();

        TypeRoom typeRoom = typeRoomService.findByName(typeRoomChose);
        Booking booking = new Booking();
        booking.setBankAccountName(bankChose);
        booking.setBankAccountNumber(accountNumber);
        booking.setCheckInDate(checkInDateConfig);
        booking.setCheckOutDate(checkOutDateConfig);
        booking.setNote(note);
        booking.setCustomer(customer);
        booking.setOrder(null);
        booking.setTypeRoom(!DataUtil.isNull(typeRoom) ? typeRoom : null);
        booking.setNumberAdults(numberCustomer);
        booking.setNumberChildren(numberChildren);
        booking.setNumberDays(numberNight);
        booking.setNumberRooms(numberRoom);
        booking.setTotalPrice(DataUtil.convertLongToBigDecimal(amount));
        booking.setRoomPrice(DataUtil.convertLongToBigDecimal(roomPrice));
        booking.setVat(DataUtil.convertLongToBigDecimal((long) (roomPrice * 0.1)));
        booking.setStatus(Constant.BOOKING.NEW);
        booking.setCreateAt(new Date());
        booking.setCreateBy(baseService.getCurrentUser().getFullname());
        booking.setUpdateAt(new Date());
        booking.setUpdatedBy(baseService.getCurrentUser().getFullname());
        bookingService.create(booking);

        // Thêm vào bảng history
        BookingHistoryTransaction bookingHistoryTransaction = new BookingHistoryTransaction();
        bookingHistoryTransaction.setIdBooking(booking.getId());
        bookingHistoryTransaction.setNote(note);
        bookingHistoryTransaction.setType(Constant.HISTORY_TYPE.CREATE);
        bookingHistoryTransaction.setTotalPrice(DataUtil.convertLongToBigDecimal(amount));
        bookingHistoryTransaction.setCreateAt(new Date());
        bookingHistoryTransaction.setCreateBy(baseService.getCurrentUser().getFullname());
        bookingHistoryTransaction.setUpdateAt(new Date());
        bookingHistoryTransaction.setUpdatedBy(baseService.getCurrentUser().getFullname());
        bookingHistoryTransactionService.create(bookingHistoryTransaction);
        return booking;
    }


    @GetMapping("/payment/done")
    @ResponseBody
    public ResponseEntity<String> vnPayDone(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Xác định xem thanh toán đã thành công hay chưa
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String amount = request.getParameter("vnp_Amount");
        String discount = request.getParameter("vnp_OrderInfo");
        String orderId = request.getParameter("vnp_TxnRef"); // Lấy mã đơn hàng từ VNPay
        if (orderId.contains("BKOL")) {
            return paymentBooking(vnp_ResponseCode, response, orderId);
        }
        String idRedirect = orderId.substring(0, 36);
        String idOrder = "";
        String idDiscount = "";
        if (orderId.length() > 36) {
            idOrder = orderId.substring(0, 36);
            idDiscount = orderId.substring(36);
        } else {
            idOrder = idRedirect;
        }
        if (vnp_ResponseCode != null && vnp_ResponseCode.equals("00")) { // Mã 00 thường tượng trưng cho thanh toán thành công
            // Thanh toán thành công, lưu thông tin vào cơ sở dữ liệu
            Order order = orderService.getOrderById(idOrder);
            if (order != null) {
                order.setTotalMoney(order.getTotalMoney().subtract(BigDecimal.valueOf(Long.parseLong(discount))));
                order.setMoneyGivenByCustomer(order.getMoneyGivenByCustomer().add(BigDecimal.valueOf(Long.parseLong(amount) / 100)));
                order.setExcessMoney(BigDecimal.valueOf(0));
                order.setDiscount(BigDecimal.valueOf(Long.parseLong(discount) / 100));
                order.setNote("Khách thanh toán bằng tài khoản ngân hàng");
                if (!idDiscount.trim().isEmpty()) {
                    order.setDiscountProgram(idDiscount);
                    discountProgramService.updateNumberOfApplication(idDiscount);
                }
                order.setUpdateAt(new Date());
                order.setStatus(Constant.ORDER_STATUS.CHECKED_OUT);
                orderService.add(order);

                Booking booking = bookingService.getByIdOrder(idOrder);
                if (booking != null) {
                    booking.setStatus(Constant.MANAGE_BOOKING.CHECKED_OUT);
                    bookingService.update(booking);
                }

                List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(order.getId());
                for (OrderDetail orderDetail : orderDetails) {
                    orderDetail.setStatus(Constant.ORDER_DETAIL.CHECKED_OUT);
                    orderDetailService.add(orderDetail);
                    Room room = orderDetail.getRoom();
                    room.setStatus(Constant.ROOM.WAIT_CLEAN);
                    roomService.add(room);
                }

                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setOrder(order);
                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                String formattedDate = currentDate.format(formatter);
                Random random = new Random();
                int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
                String paymentMethodCode = "PT" + formattedDate + randomDigits;
                paymentMethod.setPaymentMethodCode(paymentMethodCode);
                paymentMethod.setMethod(false);
                paymentMethod.setTotalMoney(order.getMoneyGivenByCustomer());
                paymentMethod.setNote(order.getNote());
                paymentMethod.setCreateAt(new Date());
                paymentMethod.setCreateBy(order.getCreateBy());
                paymentMethod.setUpdateAt(new Date());
                paymentMethod.setUpdatedBy(order.getUpdatedBy());
                paymentMethod.setStatus(Constant.COMMON_STATUS.ACTIVE);
                paymentMethodService.add(paymentMethod);

                HistoryTransaction historyTransaction = new HistoryTransaction();
                historyTransaction.setOrder(order);
                historyTransaction.setTotalMoney(order.getMoneyGivenByCustomer());
                historyTransaction.setNote(order.getNote());
                historyTransaction.setCreateAt(new Date());
                historyTransaction.setCreateBy(order.getCreateBy());
                historyTransaction.setUpdateAt(new Date());
                historyTransaction.setUpdatedBy(order.getUpdatedBy());
                historyTransaction.setStatus(Constant.COMMON_STATUS.ACTIVE);
                historyTransactionService.add(historyTransaction);

                OrderTimeline orderTimeline = new OrderTimeline();
                orderTimeline.setOrder(order);
                orderTimeline.setAccount(order.getAccount());
                orderTimeline.setType(Constant.ORDER_TIMELINE.CHECKED_OUT);
                orderTimeline.setNote("Khách chuyển khoản để thanh toán");
                orderTimeline.setCreateAt(new Date());
                orderTimelineService.add(orderTimeline);
            }
            String redirectUrl = "http://localhost:3000/order-detail?id=" + idOrder;
            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        } else {
            String redirectUrl = "http://localhost:3000/booking?id=" + idRedirect;
            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        }
    }

    private String removePrefix(String input, String prefix) {
        if (input.startsWith(prefix)) {
            return input.substring(prefix.length());
        } else {
            // Nếu chuỗi không bắt đầu bằng prefix, trả về chuỗi ban đầu
            return input;
        }
    }


    private ResponseEntity<String> paymentBooking(String vnp_ResponseCode, HttpServletResponse response, String code) throws IOException {
        System.out.println("Thành công :" + code);
        String bookingID = removePrefix(code, "BKOL");
        if (vnp_ResponseCode != null && vnp_ResponseCode.equals("00")) { // Mã 00 thường tượng trưng cho thanh toán thành công
            // Thanh toán thành công, lưu thông tin vào cơ sở dữ liệu
            // Todo cập nhật lại trạng thái của booking
            Booking booking = bookingService.findOne(bookingID);
            if (!DataUtil.isNull(booking)) {
                booking.setStatus(Constant.BOOKING.SUCCESS);
                booking.setId(bookingID);
                bookingService.create(booking);
            }
            // gửi mail
            Mail mail = new Mail();
            mail.setMailFrom("nguyenvantundz2003@gmail.com");
            mail.setMailTo(booking.getCustomer().getEmail());
            String subject = "Đặt phòng thành công ! ";
            String content = "Chúc mừng bạn đặt phòng thành công ! \n" +
                    "Thông tin đơn hàng của bạn : \n" +
                    "Tên khách hàng : " + booking.getCustomer().getFullname() + "\n" +
                    "Số điện thoại : " + booking.getCustomer().getPhoneNumber() + "\n" +
                    "Ngày đặt : " + DataUtil.convertDateToString(booking.getCreateAt()) + "\n" +
                    "Ngày CheckIn : " + DataUtil.convertDateToString(booking.getCheckInDate()) + "\n" +
                    "Ngày CheckOut : " + DataUtil.convertDateToString(booking.getCheckOutDate()) + "\n" +
                    "Tổng tiền phòng tạm tính : " + DataUtil.formatMoney(booking.getTotalPrice()) + "\n" +
                    "Loại phòng : " + booking.getTypeRoom().getTypeRoomName() + "\n" +
                    "Số lượng phòng: " + booking.getNumberRooms() + "\n" +
                    "Số người lớn : " + booking.getNumberAdults() + "\n" +
                    "Số trẻ em : " + booking.getNumberChildren() + "\n" +
                    "Vui lòng đăng nhập theo thông tin sau để theo dõi đơn booking của bạn : \n" +
                    "Email :" + booking.getCustomer().getEmail() + "\n" +
                    "Password :" + booking.getCustomer().getPassword() + "\n";

            content = content + "\n Chúc bạn có một trải nghiệm tuyệt vời ! \n";
            mail.setMailSubject(subject);
            mail.setMailContent(content);
            mailService.sendEmail(mail);
            String redirectUrl = "http://localhost:3001/success";
            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        }
        String redirectUrl = "http://localhost:3000";
        response.sendRedirect(redirectUrl);
        return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
    }

    @PostMapping("/payment-momo/{id}")
    public Map<String, Object> createPaymentMomo(@PathVariable("id") String id, @RequestBody Map<String, Object> requestBody) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        Order order = orderService.getOrderById(id);
        long amount = ((Integer) requestBody.get("amount")).longValue();
        long discount = ((Integer) requestBody.get("discount")).longValue();
        String idDiscount = (String) requestBody.get("idDiscount");
        JSONObject json = new JSONObject();
        String partnerCode = MomoConfig.PARTNER_CODE;
        String accessKey = MomoConfig.ACCESS_KEY;
        String secretKey = MomoConfig.SECRET_KEY;
        String returnUrl = MomoConfig.REDIRECT_URL;
        String notifyUrl = MomoConfig.NOTIFY_URL;
        json.put("partnerCode", partnerCode);
        json.put("accessKey", accessKey);
        json.put("requestId", String.valueOf(System.currentTimeMillis()));
        json.put("amount", Long.toString(amount));
        if (idDiscount != null) {
            json.put("orderId", order.getId() + idDiscount + generateRandomNumber(1, 100));
        } else {
            json.put("orderId", order.getId() + generateRandomNumber(1, 100));
        }
        json.put("orderInfo", Long.toString(discount));
        json.put("returnUrl", returnUrl);
        json.put("notifyUrl", notifyUrl);
        json.put("requestType", "captureMoMoWallet");

        System.out.println(json.toString());
        String data = "partnerCode=" + partnerCode
                + "&accessKey=" + accessKey
                + "&requestId=" + json.get("requestId")
                + "&amount=" + json.get("amount")
                + "&orderId=" + json.get("orderId")
                + "&orderInfo=" + json.get("orderInfo")
                + "&returnUrl=" + returnUrl
                + "&notifyUrl=" + notifyUrl
                + "&extraData=";
        System.out.println(data);
        String signature = MomoEncoderUtils.signHmacSHA256(data, secretKey);
        json.put("signature", signature);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(MomoConfig.CREATE_ORDER_URL);
        StringEntity stringEntity = new StringEntity(json.toString());
        post.setHeader("content-type", "application/json");
        post.setEntity(stringEntity);

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
            resultJsonStr.append(line);
            System.out.println(resultJsonStr);
        }
        JSONObject result = new JSONObject(resultJsonStr.toString());
        for (String key : result.keySet()) {
            System.out.format("%s = %s\n", key, result.get(key));
        }
        Map<String, Object> kq = new HashMap<String, Object>();
        if (result.get("errorCode").toString().equalsIgnoreCase("0")) {
            kq.put("requestType", result.get("requestType"));
            kq.put("orderId", result.get("orderId"));
            kq.put("payUrl", result.get("payUrl"));
            kq.put("signature", result.get("signature"));
            kq.put("requestId", result.get("requestId"));
            kq.put("errorCode", result.get("errorCode"));
            kq.put("message", Long.toString(amount));
            kq.put("localMessage", result.get("localMessage"));
        } else {
            kq.put("requestType", result.get("requestType"));
            kq.put("orderId", result.get("orderId"));
            kq.put("signature", result.get("signature"));
            kq.put("requestId", result.get("requestId"));
            kq.put("errorCode", result.get("errorCode"));
            kq.put("message", result.get("message"));
            kq.put("localMessage", result.get("localMessage"));
        }
        return kq;
    }

    @GetMapping("/payment-momo/success")
    public ResponseEntity<String> paymentMomoSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String errorCode = request.getParameter("errorCode");
        String amount = request.getParameter("amount");
        String discount = request.getParameter("orderInfo");
        String orderId = request.getParameter("orderId");
        String idOrder = "";
        String idDiscount = "";
        if (orderId.length() > 72) {
            idOrder = orderId.substring(0, 36);
            idDiscount = orderId.substring(36, 72);
        } else {
            idOrder = orderId.substring(0, 36);
        }
        if (errorCode != null && errorCode.equals("0")) { // Mã 00 thường tượng trưng cho thanh toán thành công
            // Thanh toán thành công, lưu thông tin vào cơ sở dữ liệu
            Order order = orderService.getOrderById(idOrder);
            if (order != null) {
                order.setTotalMoney(order.getTotalMoney().subtract(BigDecimal.valueOf(Long.parseLong(discount))));
                order.setMoneyGivenByCustomer(order.getMoneyGivenByCustomer().add(BigDecimal.valueOf(Long.parseLong(amount))));
                order.setExcessMoney(BigDecimal.valueOf(0));
                order.setDiscount(BigDecimal.valueOf(Long.parseLong(discount)));
                order.setNote("Khách thanh toán bằng Momo");
                if (!idDiscount.trim().isEmpty()) {
                    order.setDiscountProgram(idDiscount);
                    discountProgramService.updateNumberOfApplication(idDiscount);
                }
                order.setUpdateAt(new Date());
                order.setStatus(Constant.ORDER_STATUS.CHECKED_OUT);
                orderService.add(order);

                Booking booking = bookingService.getByIdOrder(idOrder);
                if (booking != null) {
                    booking.setStatus(Constant.MANAGE_BOOKING.CHECKED_OUT);
                    bookingService.update(booking);
                }

                List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(order.getId());
                for (OrderDetail orderDetail : orderDetails) {
                    orderDetail.setStatus(Constant.ORDER_DETAIL.CHECKED_OUT);
                    orderDetailService.add(orderDetail);
                    Room room = orderDetail.getRoom();
                    room.setStatus(Constant.ROOM.WAIT_CLEAN);
                    roomService.add(room);
                }

                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setOrder(order);
                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                String formattedDate = currentDate.format(formatter);
                Random random = new Random();
                int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
                String paymentMethodCode = "PT" + formattedDate + randomDigits;
                paymentMethod.setPaymentMethodCode(paymentMethodCode);
                paymentMethod.setMethod(false);
                paymentMethod.setTotalMoney(BigDecimal.valueOf(Long.parseLong(amount)));
                paymentMethod.setNote(order.getNote());
                paymentMethod.setCreateAt(new Date());
                paymentMethod.setCreateBy(order.getCreateBy());
                paymentMethod.setUpdateAt(new Date());
                paymentMethod.setUpdatedBy(order.getUpdatedBy());
                paymentMethod.setStatus(Constant.COMMON_STATUS.ACTIVE);
                paymentMethodService.add(paymentMethod);

                HistoryTransaction historyTransaction = new HistoryTransaction();
                historyTransaction.setOrder(order);
                historyTransaction.setTotalMoney(BigDecimal.valueOf(Long.parseLong(amount)));
                historyTransaction.setNote(order.getNote());
                historyTransaction.setCreateAt(new Date());
                historyTransaction.setCreateBy(order.getCreateBy());
                historyTransaction.setUpdateAt(new Date());
                historyTransaction.setUpdatedBy(order.getUpdatedBy());
                historyTransaction.setStatus(Constant.COMMON_STATUS.ACTIVE);
                historyTransactionService.add(historyTransaction);

                OrderTimeline orderTimeline = new OrderTimeline();
                orderTimeline.setOrder(order);
                orderTimeline.setAccount(order.getAccount());
                orderTimeline.setType(Constant.ORDER_TIMELINE.CHECKED_OUT);
                orderTimeline.setNote("Khách chuyển khoản để thanh toán");
                orderTimeline.setCreateAt(new Date());
                orderTimelineService.add(orderTimeline);
            }
            String redirectUrl = "http://localhost:3000/order-detail?id=" + idOrder;
            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        } else {
            String redirectUrl = "http://localhost:3000/booking?id=" + idOrder;
            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment failed. Back to page.");
        }
    }

    @PostMapping("/payment-momo/online/{code}")
    public Map<String, Object> createPaymentMomoOnline(@PathVariable("code") String code) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        Order order = orderService.getOrderByCode(code);
        Long amount = order.getDeposit().longValue();
        JSONObject json = new JSONObject();
        String partnerCode = MomoConfig.PARTNER_CODE;
        String accessKey = MomoConfig.ACCESS_KEY;
        String secretKey = MomoConfig.SECRET_KEY;
        String returnUrl = MomoConfig.REDIRECT_URL_ONLINE;
        String notifyUrl = MomoConfig.NOTIFY_URL;
        json.put("partnerCode", partnerCode);
        json.put("accessKey", accessKey);
        json.put("requestId", String.valueOf(System.currentTimeMillis()));
        json.put("amount", amount.toString());
        json.put("orderId", order.getId() + generateRandomNumber(1, 100));
        json.put("orderInfo", "Thanh toan don hang #" + order.getOrderCode());
        json.put("returnUrl", returnUrl);
        json.put("notifyUrl", notifyUrl);
        json.put("requestType", "captureMoMoWallet");

        System.out.println(json.toString());
        String data = "partnerCode=" + partnerCode
                + "&accessKey=" + accessKey
                + "&requestId=" + json.get("requestId")
                + "&amount=" + amount.toString()
                + "&orderId=" + json.get("orderId")
                + "&orderInfo=" + json.get("orderInfo")
                + "&returnUrl=" + returnUrl
                + "&notifyUrl=" + notifyUrl
                + "&extraData=";
        System.out.println(data);
        String signature = MomoEncoderUtils.signHmacSHA256(data, secretKey);
        json.put("signature", signature);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(MomoConfig.CREATE_ORDER_URL);
        StringEntity stringEntity = new StringEntity(json.toString());
        post.setHeader("content-type", "application/json");
        post.setEntity(stringEntity);

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
            resultJsonStr.append(line);
            System.out.println(resultJsonStr);
        }
        JSONObject result = new JSONObject(resultJsonStr.toString());
        for (String key : result.keySet()) {
            System.out.format("%s = %s\n", key, result.get(key));
        }
        Map<String, Object> kq = new HashMap<String, Object>();
        if (result.get("errorCode").toString().equalsIgnoreCase("0")) {
            kq.put("requestType", result.get("requestType"));
            kq.put("orderId", result.get("orderId"));
            kq.put("payUrl", result.get("payUrl"));
            kq.put("signature", result.get("signature"));
            kq.put("requestId", result.get("requestId"));
            kq.put("errorCode", result.get("errorCode"));
            kq.put("message", result.get("message"));
            kq.put("localMessage", result.get("localMessage"));
        } else {
            kq.put("requestType", result.get("requestType"));
            kq.put("orderId", result.get("orderId"));
            kq.put("signature", result.get("signature"));
            kq.put("requestId", result.get("requestId"));
            kq.put("errorCode", result.get("errorCode"));
            kq.put("message", result.get("message"));
            kq.put("localMessage", result.get("localMessage"));
        }
        return kq;
    }

    @GetMapping("/payment-momo/success/online")
    public ResponseEntity<String> paymentMomoSuccessOnline(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String errorCode = request.getParameter("errorCode");
        if (errorCode != null && errorCode.equals("0")) { // Mã 00 thường tượng trưng cho thanh toán thành công
            // Thanh toán thành công, lưu thông tin vào cơ sở dữ liệu
            String orderId = request.getParameter("orderId"); // Lấy mã đơn hàng từ VNPay
            if (orderId.length() > 36) {
                orderId = orderId.substring(0, 36);
            }
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                order.setNote("Khách thanh toán tiền cọc bằng Momo");
                order.setUpdateAt(new Date());
                order.setStatus(Constant.ORDER_STATUS.WAIT_CHECKIN);
                orderService.add(order);

                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setOrder(order);
                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                String formattedDate = currentDate.format(formatter);
                Random random = new Random();
                int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
                String paymentMethodCode = "PT" + formattedDate + randomDigits;
                paymentMethod.setPaymentMethodCode(paymentMethodCode);
                paymentMethod.setMethod(false);
                paymentMethod.setTotalMoney(order.getDeposit());
                paymentMethod.setNote(order.getNote());
                paymentMethod.setCreateAt(new Date());
                paymentMethod.setCreateBy(order.getCreateBy());
                paymentMethod.setUpdateAt(new Date());
                paymentMethod.setUpdatedBy(order.getUpdatedBy());
                paymentMethod.setStatus(Constant.COMMON_STATUS.ACTIVE);
                paymentMethodService.add(paymentMethod);

                HistoryTransaction historyTransaction = new HistoryTransaction();
                historyTransaction.setOrder(order);
                historyTransaction.setTotalMoney(order.getDeposit());
                historyTransaction.setNote(order.getNote());
                historyTransaction.setCreateAt(new Date());
                historyTransaction.setCreateBy(order.getCreateBy());
                historyTransaction.setUpdateAt(new Date());
                historyTransaction.setUpdatedBy(order.getUpdatedBy());
                historyTransaction.setStatus(Constant.COMMON_STATUS.ACTIVE);
                historyTransactionService.add(historyTransaction);

                OrderTimeline orderTimeline = new OrderTimeline();
                orderTimeline.setOrder(order);
                orderTimeline.setAccount(order.getAccount());
                orderTimeline.setType(Constant.ORDER_TIMELINE.WAIT_CHECKIN);
                orderTimeline.setNote("Khách chuyển khoản để thanh toán tiền cọc");
                orderTimeline.setCreateAt(new Date());
                orderTimelineService.add(orderTimeline);
            }
            String redirectUrl = "http://localhost:3001/cart";
            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        } else {
            // Thanh toán không thành công, xử lý theo logic của bạn
            String redirectUrl = "http://localhost:3001/cart";
            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        }
    }

    // truy vấn lại trạng thái thanh toán
    @PostMapping("/transactionStatus")
    public Map<String, Object> transactionStatus(@RequestParam String requestId, @RequestParam String orderId)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        JSONObject json = new JSONObject();
        String partnerCode = MomoConfig.PARTNER_CODE;
        String accessKey = MomoConfig.ACCESS_KEY;
        String secretKey = MomoConfig.SECRET_KEY;
        json.put("partnerCode", partnerCode);
        json.put("accessKey", accessKey);
        json.put("requestId", requestId);
        json.put("orderId", orderId);
        json.put("requestType", "transactionStatus");

        String data = "partnerCode=" + partnerCode + "&accessKey=" + accessKey + "&requestId=" + json.get("requestId")
                + "&orderId=" + json.get("orderId") + "&requestType=" + json.get("requestType");
        String hashData = MomoEncoderUtils.signHmacSHA256(data, secretKey);
        json.put("signature", hashData);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(MomoConfig.CREATE_ORDER_URL);
        StringEntity stringEntity = new StringEntity(json.toString());
        post.setHeader("content-type", "application/json");
        post.setEntity(stringEntity);

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }
        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> kq = new HashMap<>();
        kq.put("requestId", result.get("requestId"));
        kq.put("orderId", result.get("orderId"));
        kq.put("extraData", result.get("extraData"));
        kq.put("amount", Long.parseLong(result.get("amount").toString()));
        kq.put("transId", result.get("transId"));
        kq.put("payType", result.get("payType"));
        kq.put("errorCode", result.get("errorCode"));
        kq.put("message", result.get("message"));
        kq.put("localMessage", result.get("localMessage"));
        kq.put("requestType", result.get("requestType"));
        kq.put("signature", result.get("signature"));
        return kq;
    }

    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    @PostMapping("/payment-zalo/{id}")
    public Map<String, Object> createPaymentZaloPay(HttpServletResponse response, @PathVariable("id") String id) throws Exception {
        Order order = orderService.getOrderById(id);
        long amount = order.getTotalMoney().longValue();
        System.out.println(id);

        Map<String, Object> zalopay_Params = new HashMap<>();
        zalopay_Params.put("appid", ZaloPayConfig.APP_ID);
        zalopay_Params.put("apptransid", getCurrentTimeString("yyMMdd") + "_" + new Date().getTime());
        zalopay_Params.put("apptime", System.currentTimeMillis());
        zalopay_Params.put("pmcid", id); // Use the 'id' parameter from the path as pmcid
        zalopay_Params.put("appuser", "abc");
        zalopay_Params.put("amount", amount);
        zalopay_Params.put("description", "Thanh toan don hang #" + order.getOrderCode());
        zalopay_Params.put("bankcode", "");
        String item = "[{\"itemid\":\"knb\",\"itemname\":\"kim nguyen bao\",\"itemprice\":198400,\"itemquantity\":1}]";
        zalopay_Params.put("item", item);

        Map<String, String> embeddata = new HashMap<>();
        embeddata.put("merchantinfo", "eshop123");
        embeddata.put("promotioninfo", "");
        embeddata.put("redirecturl", ZaloPayConfig.REDIRECT_URL);

        Map<String, String> columninfo = new HashMap<String, String>();
        columninfo.put("store_name", "E-Shop");
        embeddata.put("columninfo", new JSONObject(columninfo).toString());
        zalopay_Params.put("embeddata", new JSONObject(embeddata).toString());
        System.out.println(zalopay_Params);

        String data = zalopay_Params.get("appid") + "|" + zalopay_Params.get("apptransid") + "|"
                + zalopay_Params.get("appuser") + "|" + zalopay_Params.get("amount") + "|"
                + zalopay_Params.get("apptime") + "|" + zalopay_Params.get("embeddata") + "|"
                + zalopay_Params.get("item");
        System.out.println(data);
        zalopay_Params.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, data));
        zalopay_Params.put("phone", order.getAccount().getPhoneNumber());
        zalopay_Params.put("email", order.getAccount().getEmail());
        zalopay_Params.put("address", order.getAccount().getProvinces());
        System.out.println(zalopay_Params);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ZaloPayConfig.CREATE_ORDER_URL);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : zalopay_Params.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }
        post.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse res = client.execute(post);
        System.out.println(res);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
            resultJsonStr.append(line);
            System.out.println(resultJsonStr);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> kq = new HashMap<String, Object>();
        kq.put("returnmessage", result.get("returnmessage"));
        kq.put("orderurl", result.get("orderurl"));
        kq.put("returncode", result.get("returncode"));
        kq.put("zptranstoken", result.get("zptranstoken"));
        return kq;
    }

    @GetMapping("/payment-zalo/success")
    public ResponseEntity<String> paymentZaloSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String returnCode = request.getParameter("status");
        if (returnCode != null && returnCode.equals("1")) { // Mã 00 thường tượng trưng cho thanh toán thành công
            // Thanh toán thành công, lưu thông tin vào cơ sở dữ liệu
            String orderId = request.getParameter("appuser"); // Lấy mã đơn hàng từ VNPay
            System.out.println(orderId);
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                order.setMoneyGivenByCustomer(order.getTotalMoney());
                order.setExcessMoney(BigDecimal.valueOf(0));
                order.setNote("Khách thanh toán bằng ZaloPay");
                order.setUpdateAt(new Date());
                order.setStatus(Constant.ORDER_STATUS.CHECKED_OUT);
                orderService.add(order);

                List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(order.getId());
                for (OrderDetail orderDetail : orderDetails) {
                    Room room = orderDetail.getRoom();
                    room.setStatus(Constant.ROOM.EMPTY);
                    roomService.add(room);
                }

                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setOrder(order);
                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                String formattedDate = currentDate.format(formatter);
                Random random = new Random();
                int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999
                String paymentMethodCode = "PT" + formattedDate + randomDigits;
                paymentMethod.setPaymentMethodCode(paymentMethodCode);
                paymentMethod.setMethod(false);
                paymentMethod.setTotalMoney(order.getTotalMoney());
                paymentMethod.setCreateAt(new Date());
                paymentMethod.setUpdateAt(new Date());
                paymentMethod.setStatus(Constant.COMMON_STATUS.ACTIVE);
                paymentMethodService.add(paymentMethod);

                HistoryTransaction historyTransaction = new HistoryTransaction();
                historyTransaction.setOrder(order);
                historyTransaction.setTotalMoney(order.getTotalMoney());
                historyTransaction.setNote(order.getNote());
                historyTransaction.setCreateAt(new Date());
                historyTransaction.setUpdateAt(new Date());
                historyTransaction.setStatus(Constant.COMMON_STATUS.ACTIVE);
                historyTransactionService.add(historyTransaction);

                OrderTimeline orderTimeline = new OrderTimeline();
                orderTimeline.setOrder(order);
                orderTimeline.setAccount(order.getAccount());
                orderTimeline.setType(Constant.ORDER_TIMELINE.CHECKED_OUT);
                orderTimeline.setNote("Khách chuyển khoản để thanh toán");
                orderTimeline.setCreateAt(new Date());
                orderTimelineService.add(orderTimeline);
            }

            String redirectUrl = "http://localhost:3000/order-detail?id=" + orderId;
            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        } else {
            // Thanh toán không thành công, xử lý theo logic của bạn
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        }
    }

    @PostMapping("/getstatusbyapptransid")
    public Map<String, Object> getStatusByApptransid(@RequestParam(name = "apptransid") String apptransid) throws
            Exception {
        String appid = ZaloPayConfig.APP_ID;
        String key1 = ZaloPayConfig.KEY1;
        String data = appid + "|" + apptransid + "|" + key1; // appid|apptransid|key1
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appid", appid));
        params.add(new BasicNameValuePair("apptransid", apptransid));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder("https://sandbox.zalopay.com.vn/v001/tpe/getstatusbyapptransid");
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri.build());

        CloseableHttpResponse res = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> kq = new HashMap<String, Object>();
        kq.put("returncode", result.get("returncode"));
        kq.put("returnmessage", result.get("returnmessage"));
        kq.put("isprocessing", result.get("isprocessing"));
        kq.put("amount", result.get("amount"));
        kq.put("discountamount", result.get("discountamount"));
        kq.put("zptransid", result.get("zptransid"));
        return kq;
    }

}
