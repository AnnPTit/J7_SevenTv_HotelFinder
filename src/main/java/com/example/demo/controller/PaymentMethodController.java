package com.example.demo.controller;

import com.example.demo.config.VNPayConfig;
import com.example.demo.entity.HistoryTransaction;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.OrderTimeline;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.entity.Room;
import com.example.demo.service.AccountService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.HistoryTransactionService;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderService;
import com.example.demo.service.OrderTimelineService;
import com.example.demo.service.PaymentMethodService;
import com.example.demo.service.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

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

    @GetMapping("/load/{id}")
    public List<PaymentMethod> loadByOrderId(@PathVariable("id") String id) {
        return paymentMethodService.getAllByOrderId(id);
    }

    @PostMapping("/payment/{id}")
    @ResponseBody
    public ResponseEntity<?> vnPayPost(HttpServletRequest req, @PathVariable("id") String id) throws UnsupportedEncodingException {
        Order order = orderService.getOrderById(id);
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        long amount = (order.getTotalMoney().longValue() * 100);

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(req);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", order.getId());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + order.getOrderCode());
        vnp_Params.put("vnp_OrderType", "billpayment1231");
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

    @GetMapping("/payment/done")
    @ResponseBody
    public ResponseEntity<String> vnPayDone(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Xác định xem thanh toán đã thành công hay chưa
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        if (vnp_ResponseCode != null && vnp_ResponseCode.equals("00")) { // Mã 00 thường tượng trưng cho thanh toán thành công
            // Thanh toán thành công, lưu thông tin vào cơ sở dữ liệu
            String orderId = request.getParameter("vnp_TxnRef"); // Lấy mã đơn hàng từ VNPay
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                order.setMoneyGivenByCustomer(order.getTotalMoney());
                order.setExcessMoney(BigDecimal.valueOf(0));
                order.setNote("Khách thanh toán bằng NCB");
                order.setUpdateAt(new Date());
                order.setStatus(3);
                orderService.add(order);

                List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(order.getId());
                for (OrderDetail orderDetail : orderDetails) {
                    Room room = orderDetail.getRoom();
                    room.setStatus(1);
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
                paymentMethod.setStatus(1);
                paymentMethodService.add(paymentMethod);

                HistoryTransaction historyTransaction = new HistoryTransaction();
                historyTransaction.setOrder(order);
                historyTransaction.setTotalMoney(order.getTotalMoney());
                historyTransaction.setNote(order.getNote());
                historyTransaction.setCreateAt(new Date());
                historyTransaction.setUpdateAt(new Date());
                historyTransaction.setStatus(1);
                historyTransactionService.add(historyTransaction);

                OrderTimeline orderTimeline = new OrderTimeline();
                orderTimeline.setOrder(order);
                orderTimeline.setAccount(order.getAccount());
                orderTimeline.setType(3);
                orderTimeline.setNote("Khách chuyển khoản để thanh toán");
                orderTimeline.setCreateAt(new Date());
                orderTimelineService.add(orderTimeline);
            }
                String redirectUrl = "http://localhost:3000/orders?id=" + orderId;
                response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        } else {
            // Thanh toán không thành công, xử lý theo logic của bạn
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        }
    }

}
