package com.example.demo.controller;

import com.example.demo.config.MomoConfig;
import com.example.demo.config.VNPayConfig;
import com.example.demo.config.ZaloPayConfig;
import com.example.demo.constant.Constant;
import com.example.demo.entity.HistoryTransaction;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.OrderTimeline;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.entity.Room;
import com.example.demo.service.HistoryTransactionService;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderService;
import com.example.demo.service.OrderTimelineService;
import com.example.demo.service.PaymentMethodService;
import com.example.demo.service.RoomService;
import com.example.demo.util.HMACUtil;
import com.example.demo.util.MomoEncoderUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
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

    @GetMapping("/loadAndSearch")
    public Page<PaymentMethod> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                             @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return paymentMethodService.loadAndSearch(key, pageable);
    }

    @GetMapping("/load/{id}")
    public List<PaymentMethod> loadByOrderId(@PathVariable("id") String id) {
        return paymentMethodService.getAllByOrderId(id);
    }

    @PostMapping("/payment-vnpay/{id}")
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
            String redirectUrl = "http://localhost:3000/orders?id=" + orderId;
            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        } else {
            // Thanh toán không thành công, xử lý theo logic của bạn
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        }
    }

    @PostMapping("/payment-momo/{id}")
    public Map<String, Object> createPaymentMomo(@PathVariable("id") String id) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        Order order = orderService.getOrderById(id);
        Long amount = order.getTotalMoney().longValue();
        JSONObject json = new JSONObject();
        String partnerCode = MomoConfig.PARTNER_CODE;
        String accessKey = MomoConfig.ACCESS_KEY;
        String secretKey = MomoConfig.SECRET_KEY;
        String returnUrl = MomoConfig.REDIRECT_URL;
        String notifyUrl = MomoConfig.NOTIFY_URL;
        json.put("partnerCode", partnerCode);
        json.put("accessKey", accessKey);
        json.put("requestId", String.valueOf(System.currentTimeMillis()));
        json.put("amount", amount.toString());
        json.put("orderId", order.getId());
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

    @GetMapping("/payment-momo/success")
    public ResponseEntity<String> paymentMomoSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String errorCode = request.getParameter("errorCode");
        if (errorCode != null && errorCode.equals("0")) { // Mã 00 thường tượng trưng cho thanh toán thành công
            // Thanh toán thành công, lưu thông tin vào cơ sở dữ liệu
            String orderId = request.getParameter("orderId"); // Lấy mã đơn hàng từ VNPay
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                order.setMoneyGivenByCustomer(order.getTotalMoney());
                order.setExcessMoney(BigDecimal.valueOf(0));
                order.setNote("Khách thanh toán bằng Momo");
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
            String redirectUrl = "http://localhost:3000/orders?id=" + orderId;
            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok("Payment successful. Redirect to confirmation page.");
        } else {
            // Thanh toán không thành công, xử lý theo logic của bạn
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

    @PostMapping("/payment-zalo/{id}")
    public Map<String, Object> createPaymentZaloPay(HttpServletResponse response, @PathVariable("id") String id) throws Exception {
        Order order = orderService.getOrderById(id);
        long amount = order.getTotalMoney().longValue();

        Map<String, Object> zalopay_Params = new HashMap<>();
        zalopay_Params.put("appid", ZaloPayConfig.APP_ID);
        zalopay_Params.put("apptransid", getCurrentTimeString("yyMMdd") + "_" + new Date().getTime());
        zalopay_Params.put("apptime", System.currentTimeMillis());
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

            String redirectUrl = "http://localhost:3000/orders?id=" + orderId;
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