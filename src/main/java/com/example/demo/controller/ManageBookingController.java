package com.example.demo.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.demo.config.S3Util;
import com.example.demo.constant.Constant;
import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.CustomerBookingDTO;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingHistoryTransaction;
import com.example.demo.entity.Customer;
import com.example.demo.entity.HistoryTransaction;
import com.example.demo.entity.InformationCustomer;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.service.BookingHistoryTransactionService;
import com.example.demo.service.BookingService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.HistoryTransactionService;
import com.example.demo.service.InformationCustomerService;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderService;
import com.example.demo.service.PaymentMethodService;
import com.example.demo.util.BaseService;
import com.example.demo.util.DataUtil;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/manage-booking")
public class ManageBookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private InformationCustomerService informationCustomerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private BookingHistoryTransactionService bookingHistoryTransactionService;
    @Autowired
    private PaymentMethodService paymentMethodService;
    @Autowired
    private HistoryTransactionService historyTransactionService;
    @Autowired
    private BaseService baseService;


    private static String bucketName = "j7v1"; // Thay bằng tên bucket AWS S3 của bạn
    private static String accessKey = "AKIAYEQDRZP5KHP3T2EK";
    private static String secretKey = "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";
    private static String region = "us-east-1"; // Ví dụ: "ap-southeast-1"
    @Autowired
    public S3Util s3Util;


    @GetMapping("/load")
    public Page<Booking> findAll(@RequestParam(name = "key", defaultValue = "") String key,
                                 @RequestParam(name = "status", defaultValue = "") Integer status,
                                 @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return bookingService.findAll(key, key, key, status, pageable);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Booking> getById(@PathVariable("id") String id) {
        Booking booking = bookingService.getById(id);
        return new ResponseEntity<Booking>(booking, HttpStatus.OK);
    }

    @DeleteMapping("/change-status/{id}")
    public ResponseEntity<String> changeStatus(@PathVariable("id") String id) {
        Booking booking = bookingService.getById(id);
        booking.setStatus(Constant.MANAGE_BOOKING.ACTIVE);
        bookingService.update(booking);
        return new ResponseEntity<String>("Success " + id + " successfully", HttpStatus.OK);
    }

    @DeleteMapping("/change-wait-room/{id}")
    public ResponseEntity<String> changeWaitRoom(@PathVariable("id") String id) {
        Booking booking = bookingService.getById(id);
        booking.setStatus(Constant.MANAGE_BOOKING.WAIT_ROOM);
        bookingService.update(booking);
        return new ResponseEntity<String>("Success " + id + " successfully", HttpStatus.OK);
    }

    @DeleteMapping("/change-cancel/{id}")
    public ResponseEntity<String> changeCancel(@PathVariable("id") String id) {
        Booking booking = bookingService.getById(id);
        booking.setStatus(Constant.MANAGE_BOOKING.UNACTIVE);
        bookingService.update(booking);
        return new ResponseEntity<String>("Success " + id + " successfully", HttpStatus.OK);
    }

    @PostMapping("/accept-customer")
    public ResponseEntity<?> acceptCustomer(@RequestBody CustomerBookingDTO customerBookingDTO) {
        Customer existingCustomer = customerService.findByCitizenId(customerBookingDTO.getCitizenId());

        if (existingCustomer != null) {
            return new ResponseEntity<String>("Giấy tờ tùy thân đã tồn tại.", HttpStatus.BAD_REQUEST);
        }

        Customer customer = customerService.getCustomerById(customerBookingDTO.getIdCustomer());
        customer.setCitizenId(customerBookingDTO.getCitizenId());
        customer.setBirthday(customerBookingDTO.getBirthday());
        customer.setGender(customerBookingDTO.getGender());
        customer.setNationality(customerBookingDTO.getNationality());
        customer.setAddress(customerBookingDTO.getAddress());
        customerService.add(customer);

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(customerBookingDTO.getIdOrder());
        addInformationCustomer(customer, orderDetails);

        Booking booking = bookingService.getById(customerBookingDTO.getIdBooking());
        booking.setStatus(Constant.MANAGE_BOOKING.CHECKED_IN);
        bookingService.update(booking);

        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    public void addInformationCustomer(Customer customer, List<OrderDetail> orderDetails) {
        InformationCustomer informationCustomer = new InformationCustomer();
        informationCustomer.setFullname(customer.getFullname());
        informationCustomer.setEmail(customer.getEmail());
        informationCustomer.setPhoneNumber(customer.getPhoneNumber());
        informationCustomer.setCitizenId(customer.getCitizenId());
        informationCustomer.setBirthday(customer.getBirthday());
        informationCustomer.setGender(customer.getGender());
        informationCustomer.setNationality(customer.getNationality());
        informationCustomer.setAddress(customer.getAddress());
        informationCustomer.setCreateAt(new Date());
        informationCustomer.setUpdateAt(new Date());
        informationCustomer.setStatus(Constant.COMMON_STATUS.ACTIVE);
        informationCustomer.setOrderDetail(orderDetails.get(0));
        informationCustomerService.add(informationCustomer);
    }

    @PutMapping("/cancel-booking/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable("id") String id, @RequestBody BookingDTO bookingDTO) {
        if (StringUtils.isBlank(bookingDTO.getBankAccountName())) {
            return new ResponseEntity<>("Tên ngân hàng không được để trống", HttpStatus.BAD_REQUEST);
        } else if (StringUtils.isBlank(bookingDTO.getBankAccountNumber())) {
            return new ResponseEntity<>("Số tài khoản không được để trống", HttpStatus.BAD_REQUEST);
        } else if (!isNumeric(bookingDTO.getBankAccountNumber()) || bookingDTO.getBankAccountNumber().length() < 9 || bookingDTO.getBankAccountNumber().length() > 20) {
            return new ResponseEntity<>("Số tài khoản phải từ 9-20 số", HttpStatus.BAD_REQUEST);
        }

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formattedDate = currentDate.format(formatter);
        Random random = new Random();
        int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999

        Booking booking = bookingService.getById(id);
        booking.setStatus(Constant.MANAGE_BOOKING.UNACTIVE);
        booking.setBankAccountName(bookingDTO.getBankAccountName());
        booking.setBankAccountNumber(bookingDTO.getBankAccountNumber());
        bookingService.update(booking);

        BookingHistoryTransaction bookingHistoryTransaction = new BookingHistoryTransaction();
        bookingHistoryTransaction.setIdBooking(id);
        bookingHistoryTransaction.setPaymentMethod(Constant.COMMON_STATUS.ACTIVE);
        bookingHistoryTransaction.setType(Constant.HISTORY_TYPE.CANCEL);
        bookingHistoryTransaction.setCancelReason(bookingDTO.getNote());
        bookingHistoryTransaction.setCancelDate(new Date());
        bookingHistoryTransaction.setRefundMoney(booking.getTotalPrice());
        bookingHistoryTransaction.setCreateAt(new Date());
        bookingHistoryTransaction.setCreateBy(baseService.getCurrentUser().getFullname());
        bookingHistoryTransaction.setUpdateAt(new Date());
        bookingHistoryTransaction.setUpdatedBy(baseService.getCurrentUser().getFullname());
        bookingHistoryTransaction.setStatus(Constant.COMMON_STATUS.UNACTIVE);
        bookingHistoryTransactionService.create(bookingHistoryTransaction);

        if (booking.getOrder() != null) {
            Order order = orderService.getOrderById(booking.getOrder().getId());
            order.setStatus(Constant.ORDER_STATUS.CANCEL);
            orderService.add(order);

            List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(order.getId());
            if (!orderDetails.isEmpty()) {
                for (OrderDetail orderDetail : orderDetails) {
                    orderDetail.setStatus(Constant.ORDER_DETAIL.CANCEL);
                    orderDetail.getRoom().setStatus(Constant.ROOM.EMPTY);
                    orderDetailService.add(orderDetail);
                }
            }

            HistoryTransaction historyTransaction = new HistoryTransaction();
            historyTransaction.setOrder(order);
            historyTransaction.setTotalMoney(booking.getTotalPrice());
            historyTransaction.setCreateAt(new Date());
            historyTransaction.setCreateBy(baseService.getCurrentUser().getFullname());
            historyTransaction.setUpdateAt(new Date());
            historyTransaction.setUpdatedBy(baseService.getCurrentUser().getFullname());
            historyTransaction.setStatus(Constant.COMMON_STATUS.UNACTIVE);
            historyTransactionService.add(historyTransaction);

            String paymentMethodCode = "PT" + formattedDate + randomDigits;

            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setOrder(order);
            paymentMethod.setMethod(false);
            paymentMethod.setPaymentMethodCode(paymentMethodCode);
            paymentMethod.setNote(bookingDTO.getNote());
            paymentMethod.setTotalMoney(booking.getTotalPrice());
            paymentMethod.setCreateAt(new Date());
            paymentMethod.setCreateBy(baseService.getCurrentUser().getFullname());
            paymentMethod.setUpdateAt(new Date());
            paymentMethod.setUpdatedBy(baseService.getCurrentUser().getFullname());
            paymentMethod.setStatus(Constant.COMMON_STATUS.UNACTIVE);
            paymentMethodService.add(paymentMethod);
        }

        return new ResponseEntity<Booking>(booking, HttpStatus.OK);
    }

    @PutMapping("/cancel-booking/customer/{id}")
    public ResponseEntity<?> cancelBooking2(@RequestParam("id") String id,
                                            @RequestParam("bankAccountName") String bankAccountName,
                                            @RequestParam("bankAccountNumber") String bankAccountNumber,
                                            @RequestParam("note") String note,
                                            @RequestPart("file") MultipartFile file) throws IOException {
        System.out.println(file);
        if (StringUtils.isBlank(bankAccountName)) {
            return new ResponseEntity<>("Tên ngân hàng không được để trống", HttpStatus.BAD_REQUEST);
        } else if (StringUtils.isBlank(bankAccountNumber)) {
            return new ResponseEntity<>("Số tài khoản không được để trống", HttpStatus.BAD_REQUEST);
        } else if (!isNumeric(bankAccountNumber) || bankAccountNumber.length() < 9 || bankAccountNumber.length() > 20) {
            return new ResponseEntity<>("Số tài khoản phải từ 9-20 số", HttpStatus.BAD_REQUEST);
        } else if (file == null) {
            return new ResponseEntity<>("Hình ảnh bằng chứng không được để trống !", HttpStatus.BAD_REQUEST);
        }


        // Validate file format based on file extension
        if (!isValidImageFormat(file)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chỉ hỗ trợ file PNG và JPG");
        }
        String url = uploadFile(file);
        if (url == null) {
            return new ResponseEntity<>("Upload thất bại", HttpStatus.BAD_REQUEST);
        }

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formattedDate = currentDate.format(formatter);
        Random random = new Random();
        int randomDigits = random.nextInt(90000) + 10000; // Sinh số ngẫu nhiên từ 10000 đến 99999

        Booking booking = bookingService.getById(id);
//
//
        BookingHistoryTransaction bookingHistoryTransaction = new BookingHistoryTransaction();
        bookingHistoryTransaction.setIdBooking(booking.getId());
        bookingHistoryTransaction.setPaymentMethod(Constant.COMMON_STATUS.ACTIVE);
        bookingHistoryTransaction.setType(Constant.HISTORY_TYPE.CANCEL);
        bookingHistoryTransaction.setCancelReason(note);
        bookingHistoryTransaction.setCancelDate(new Date());
        bookingHistoryTransaction.setRefundMoney(booking.getTotalPrice());
        bookingHistoryTransaction.setCreateAt(new Date());
        bookingHistoryTransaction.setCreateBy(baseService.getCurrentUser().getFullname());
        bookingHistoryTransaction.setUpdateAt(new Date());
        bookingHistoryTransaction.setUpdatedBy(baseService.getCurrentUser().getFullname());
        bookingHistoryTransaction.setStatus(Constant.COMMON_STATUS.UNACTIVE);
        bookingHistoryTransaction.setUrl(url);
        bookingHistoryTransactionService.create(bookingHistoryTransaction);

        if (booking.getOrder() != null) {
            Order order = orderService.getOrderById(booking.getOrder().getId());
            order.setStatus(Constant.ORDER_STATUS.CANCEL);
            orderService.add(order);

            List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(order.getId());
            if (!orderDetails.isEmpty()) {
                for (OrderDetail orderDetail : orderDetails) {
                    orderDetail.setStatus(Constant.ORDER_DETAIL.CANCEL);
                    orderDetail.getRoom().setStatus(Constant.ROOM.EMPTY);
                    orderDetailService.add(orderDetail);
                }
            }

            HistoryTransaction historyTransaction = new HistoryTransaction();
            historyTransaction.setOrder(order);
            historyTransaction.setTotalMoney(booking.getTotalPrice());
            historyTransaction.setCreateAt(new Date());
            historyTransaction.setCreateBy(baseService.getCurrentUser().getFullname());
            historyTransaction.setUpdateAt(new Date());
            historyTransaction.setUpdatedBy(baseService.getCurrentUser().getFullname());
            historyTransaction.setStatus(Constant.COMMON_STATUS.UNACTIVE);
            historyTransactionService.add(historyTransaction);

            String paymentMethodCode = "PT" + formattedDate + randomDigits;

            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setOrder(order);
            paymentMethod.setMethod(false);
            paymentMethod.setPaymentMethodCode(paymentMethodCode);
            paymentMethod.setNote(note);
            paymentMethod.setTotalMoney(booking.getTotalPrice());
            paymentMethod.setCreateAt(new Date());
            paymentMethod.setCreateBy(baseService.getCurrentUser().getFullname());
            paymentMethod.setUpdateAt(new Date());
            paymentMethod.setUpdatedBy(baseService.getCurrentUser().getFullname());
            paymentMethod.setStatus(Constant.COMMON_STATUS.UNACTIVE);
            paymentMethodService.add(paymentMethod);

        }
        booking.setStatus(Constant.BOOKING.CANCELED);
        booking.setBankAccountName(bankAccountName);
        booking.setBankAccountNumber(bankAccountNumber);
        bookingHistoryTransaction.setUrl(url);
        bookingService.update(booking);


        return new ResponseEntity<Booking>(new Booking(), HttpStatus.OK);
    }


    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidImageFormat(MultipartFile file) {
        // Get the original file name
        String originalFilename = file.getOriginalFilename();

        // Check if the file name is not null and has a valid image extension
        if (originalFilename != null) {
            String fileExtension = getFileExtension(originalFilename.toLowerCase());
            return "png".equals(fileExtension) || "jpg".equals(fileExtension) || "jpeg".equals(fileExtension);
        }

        return false;
    }

    private String getFileExtension(String filename) {
        // Get the file extension from the filename
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return filename.substring(lastDotIndex + 1);
        }
        return null;
    }


    public String uploadFile(MultipartFile file) throws IOException {
        try {
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
            return imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}