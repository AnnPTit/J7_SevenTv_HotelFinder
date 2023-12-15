package com.example.demo.service.impl;

import com.example.demo.constant.Constant;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.errors.BadRequestAlertException;
import com.example.demo.model.Mail;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderTimelineRepository;
import com.example.demo.service.MailService;
import com.example.demo.service.OrderService;
import com.example.demo.util.BaseService;
import com.example.demo.util.DataUtil;
import com.example.demo.util.NumToViet;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderTimelineRepository orderTimelineRepository;
    private NumToViet numToViet;
    private final MailService mailService;
    @Autowired
    private BaseService baseService;


    @Override
    public List<Order> getList() {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> getAllByStatus(Pageable pageable) {
        return orderRepository.findAllByStatus(pageable);
    }


    @Override
    public Page<Order> loadAndSearch(String orderCode, Boolean typeOfOrder, Integer status, String customerFullname, Date startDate, Date endDate, Pageable pageable) {
        return orderRepository.loadAndSearch((orderCode != null && !orderCode.isEmpty()) ? orderCode : null,
                (typeOfOrder != null && !typeOfOrder.toString().isEmpty()) ? typeOfOrder : null,
                (status != null && !status.toString().isEmpty()) ? status : null,
                (customerFullname != null && !customerFullname.isEmpty()) ? customerFullname : null,
                startDate,
                endDate,
                pageable);
    }

    @Override
    public Page<Order> loadBookRoomOnline(String orderCode, String customerFullname, String customerPhone, String customerEmail, Integer status, Pageable pageable) {
        return orderRepository.loadBookRoomOnline((orderCode != null && !orderCode.isEmpty()) ? orderCode : null,
                (customerFullname != null && !customerFullname.isEmpty()) ? customerFullname : null,
                (customerPhone != null && !customerPhone.isEmpty()) ? customerPhone : null,
                (customerEmail != null && !customerEmail.isEmpty()) ? customerEmail : null,
                (status != null && !status.toString().isEmpty()) ? status : null, pageable);
    }

    @Override
    public Page<Order> loadBookRoomOffline(String orderCode, Integer status, Pageable pageable) {
        return orderRepository.loadBookRoomOffline((orderCode != null && !orderCode.isEmpty()) ? orderCode : null,
                (status != null && !status.toString().isEmpty()) ? status : null, pageable);
    }

    @Override
    public List<Order> loadNotify() {
        return orderRepository.loadNotify();
    }

    @Override
    public Order getOrderById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order getOrderByCode(String code) {
        return orderRepository.getByCode(code);
    }

    @Override
    public Long countOrderCancel() {
        return orderRepository.countOrderCancel();
    }

    @Override
    public Long countOrderWait() {
        return orderRepository.countOrderWait();
    }

    @Override
    public Long countOrderConfirm() {
        return orderRepository.countOrderConfirm();
    }

    @Override
    public Long countOrderAccept() {
        return orderRepository.countOrderAccept();
    }

    @Override
    public Order add(Order order) {
        try {
            return orderRepository.save(order);
        } catch (Exception e) {
            System.out.println("Add error!");
            return null;
        }
    }

    @Override
    public List<Order> add(List<Order> orders) {
        try {
            return orderRepository.saveAll(orders);
        } catch (Exception e) {
            System.out.println("Add error!");
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete error!");
        }
    }

    @Override
    public ConfirmOrderDTO confirmOrder(ConfirmOrderDTO confirmOrderDTO) {

        // kiểm tra căn cước công dân trùng nhau
        Customer customer1 = customerRepository.getCustomerById(confirmOrderDTO.getCustomerId());
        // Kiểm tra trống
        if (DataUtil.isNull(confirmOrderDTO.getCitizenId())) {
            ConfirmOrderDTO confirmOrderDTO1 = new ConfirmOrderDTO();
            confirmOrderDTO1.setMessage("Căn cước công dân không được để trống !");
            return confirmOrderDTO1;
        }
        // kiểm tra định dạng
        if (!confirmOrderDTO.getCitizenId().matches("\\d{12}")) {
            ConfirmOrderDTO confirmOrderDTO1 = new ConfirmOrderDTO();
            confirmOrderDTO1.setMessage("Căn cước công dân không đúng định dạng !");
            return confirmOrderDTO1;
        }
        if (customer1.getCitizenId() != null && customer1.getCitizenId().equals(confirmOrderDTO.getCitizenId()) && confirmOrderDTO.getIsNewCustomer() == true) {
            // kiểm tra -> không được trùng
            ConfirmOrderDTO confirmOrderDTO1 = new ConfirmOrderDTO();
            confirmOrderDTO1.setMessage("Căn cước công dân không được trùng nhau !");
            return confirmOrderDTO1;
        }
        // Cập nhật trạng thái order
        // Thêm hạn thanh toán tiền cọc
        // Hạn thanh toán sau ngày xác nhận 1 ngày
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        // Get the updated date
        Date updatedDate = calendar.getTime();
        Order order = orderRepository.getById(confirmOrderDTO.getOrderId());
        order.setCreateBy(baseService.getCurrentUser().getFullname());
        Account account = new Account();
        account.setId(baseService.getCurrentUser().getId());
        order.setAccount(account);
        order.setPaymentDeadline(updatedDate);
        order.setStatus(Constant.ORDER_STATUS.WAIT_PAYMENT);
        orderRepository.save(order);
        // Tạo timeline
        OrderTimeline orderTimeline = new OrderTimeline();
        orderTimeline.setOrder(order);
        orderTimeline.setAccount(order.getAccount());
        orderTimeline.setType(Constant.ORDER_TIMELINE.WAIT_PAYMENT);
        orderTimeline.setNote("Xác nhận thông tin khách booking");
        orderTimeline.setCreateAt(new Date());
        orderTimelineRepository.save(orderTimeline);
        // Cập nhật trạng thông tin khách hàng
        Customer customer = customerRepository.getCustomerById(confirmOrderDTO.getCustomerId());
        customer.setAddress(confirmOrderDTO.getAddress());
        String birthday = confirmOrderDTO.getBirthday();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            customer.setBirthday(date);
        } else {
            System.out.println("Không thể chuyển đổi ngày sinh.");
        }
        customer.setNationality(confirmOrderDTO.getNation());
        customer.setGender(confirmOrderDTO.isGender());
        customer.setCitizenId(confirmOrderDTO.getCitizenId());
        customerRepository.save(customer);
        // Gui mail
        List<OrderDetail> orderDetails = order.getOrderDetailList();
        String mailTo = customer.getEmail();
        String subJect = "Xác Nhận Đơn Đặt Phòng của Quý Khách tại Khách Sạn Armani";
        String subContent = "";
        for (OrderDetail orderDetail : orderDetails) {
            subContent = subContent + " \n Loại phòng: " + orderDetail.getRoom().getTypeRoom().getTypeRoomName() + "\n" +
                    "Số lượng khách: " + orderDetail.getCustomerQuantity() + "\n";
        }
        String content = "Kính gửi Quý Khách,\n" +
                "\n" +
                "Chúng tôi xin chân thành cảm ơn Quý Khách đã chọn Khách Sạn Armani là điểm đến lưu trú của mình. Để đảm bảo rằng trải nghiệm của Quý Khách sẽ được chu đáo và không gian lưu trú tuyệt vời, chúng tôi xác nhận thông tin đơn đặt phòng như sau:\n" +
                "\n" +
                "Thông tin đặt phòng:\n" +
                "\n" +
                "Tên Khách hàng: " + customer.getFullname() + "\n" +
                "Ngày nhận phòng: " + DataUtil.dateToString(order.getBookingDateStart()) + "\n" +
                "Ngày trả phòng: " + DataUtil.dateToString(order.getBookingDateEnd()) + "\n" +
                "Chi Tiết : " +
                subContent +
                " \nThông tin liên hệ:\n" +
                "\n" +
                "Địa chỉ email: " + customer.getEmail() + "\n" +
                "Số điện thoại liên lạc: " + customer.getPhoneNumber() + "\n" +
                "Ghi chú đặc biệt (nếu có):\n" +
                order.getNote() + "\n" +
                "\n" +
                "Chúng tôi đã nhận được thanh toán của Quý Khách và đang chấp nhận đơn đặt phòng của Quý Khách.\n" +
                "\n" +
                "Để giúp Quý Khách có trải nghiệm lưu trú tuyệt vời nhất, chúng tôi mong muốn được phục vụ các yêu cầu đặc biệt nếu có. Xin vui lòng liên hệ với chúng tôi qua email hoặc số điện thoại trước ngày nhận phòng nếu có bất kỳ thay đổi hoặc điều chỉnh nào.\n" +
                "\n" +
                "Chúng tôi rất mong đợi được phục vụ Quý Khách tại Khách Sạn Armani và hy vọng rằng chuyến đi của Quý Khách sẽ trở thành một kỷ niệm đáng nhớ.\n" +
                "\n" +
                "Trân trọng,\n" +
                "[Armani Hotel]\n" +
                "Khách Sạn Armani\n";

        DataUtil.sendMailCommon(mailTo, subJect, content, mailService);
        return confirmOrderDTO;
    }

    @Override
    public ConfirmOrderDTO updateSurcharge(ConfirmOrderDTO confirmOrderDTO) {
        Order order = orderRepository.getById(confirmOrderDTO.getOrderId());
        order.setSurcharge(confirmOrderDTO.getSurcharge());
        orderRepository.save(order);
        return confirmOrderDTO;
    }

    @Override
    public BigDecimal getRevenueMonth() {
        return orderRepository.getRevenueMonth();
    }

    @Override
    public BigDecimal getRevenueYear() {
        return orderRepository.getRevenueYear();
    }

    @Override
    public ByteArrayResource exportRecommended(String orderId) throws JRException {
        OrderExportDTO orderExportDTO = orderRepository.getData(orderId);
        List<OrderDetailExport> dataTable = orderRepository.getDataDetail(orderId);
        List<ServiceUsedInvoiceDTO> serviceUsedInvoiceDTOS = new ArrayList<>();
        serviceUsedInvoiceDTOS.addAll(orderRepository.getService(orderId));
        serviceUsedInvoiceDTOS.addAll(orderRepository.getCombo(orderId));
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        int year = zonedDateTime.getYear();
        int month = zonedDateTime.getMonthValue();
        int day = zonedDateTime.getDayOfMonth();
        BigDecimal totalPriceService = BigDecimal.ZERO;
        BigDecimal totalPriceRoom = BigDecimal.ZERO;
        for (ServiceUsedInvoiceDTO serviceUsedInvoiceDTO : serviceUsedInvoiceDTOS) {
            totalPriceService = totalPriceService.add(serviceUsedInvoiceDTO.getTotal3());
        }
        for (OrderDetailExport export : dataTable) {
            totalPriceRoom = totalPriceRoom.add(export.getTotalPrice2());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream employeeReportStream = getClass().getResourceAsStream("/templates/doc/recommended.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("logoPath", getClass().getResourceAsStream("/templates/image/logo.png"));
        parameters.put("code", orderExportDTO.getCode());
        parameters.put("deliverer", orderExportDTO.getCreater());
        parameters.put("customer", orderExportDTO.getCustomer());
        parameters.put("bookingDay", DataUtil.dateToString(orderExportDTO.getBookingDay()));
        parameters.put("checkin", orderExportDTO.getCheckIn() != null ? DataUtil.dateToString(orderExportDTO.getCheckIn()) : dataTable.get(0).getCheckIn());
        parameters.put("checkOut", orderExportDTO.getCheckOut() != null ? DataUtil.dateToString(orderExportDTO.getCheckIn()) : dataTable.get(0).getCheckOut());
        parameters.put("day", day);
        parameters.put("month", month);
        parameters.put("year", year);
        parameters.put("deposit", orderExportDTO.getDeposit());
        parameters.put("monneyCustom", orderExportDTO.getMonneyCustom());
        parameters.put("totalMoney", DataUtil.currencyFormat(orderExportDTO.getTotalMoney()));
        parameters.put("excessMoney", orderExportDTO.getExcessMoney());
        parameters.put("surcharge", orderExportDTO.getSurcharge());
        parameters.put("totalPriceService", DataUtil.currencyFormat(totalPriceService));
        parameters.put("note", StringUtils.isNotBlank(orderExportDTO.getNote()) ? orderExportDTO.getNote() : "");
        long total = orderExportDTO.getTotalMoney().longValue();
        String totalPriceString = numToViet.num2String(total) + " đồng";
        totalPriceString = totalPriceString.substring(0, 1).toUpperCase() + totalPriceString.substring(1);
        parameters.put("stringTotalPrice", totalPriceString);
        parameters.put("totalNumberPrice", DataUtil.currencyFormat(orderExportDTO.getTotalMoney()));
        parameters.put("total", DataUtil.currencyFormat(totalPriceRoom));
        parameters.put("vat", DataUtil.currencyFormat(orderExportDTO.getVat()));
        parameters.put("discount", DataUtil.currencyFormat(orderExportDTO.getDiscount()));
        parameters.put("Parameter1", new JRBeanCollectionDataSource(serviceUsedInvoiceDTOS));
        parameters.put("dataTable", new JRBeanCollectionDataSource(dataTable));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JRDocxExporter export = new JRDocxExporter();
        export.setExporterInput(new SimpleExporterInput(jasperPrint));
        export.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        export.exportReport();
        return new ByteArrayResource(baos.toByteArray());
    }

    @Override
    public List<RevenueDTO> getRevenue() {
        return orderRepository.getRevenue();
    }

    @Override
    public void refuse(String id, Integer stt, String refuseReason) {
        Order order = orderRepository.getById(id);
        if (DataUtil.isNull(order)) {
            return;
        }
        String mailTo = order.getCustomer().getEmail();
        String subject = "Thông báo từ Chúng tôi về Đơn Đặt Phòng của Quý Khách";
        String content = "Chào Quý khách " + order.getCustomer().getFullname() +
                "\n" +
                "Chúng tôi xin trân trọng gửi lời cảm ơn chân thành về sự quan tâm và lựa chọn dịch vụ của Armani Hotel. Rất tiếc, sau khi kiểm tra, chúng tôi phải thông báo rằng chúng tôi không thể chấp nhận được đơn đặt phòng của Quý khách vào thời gian mong muốn.\n" +
                "\n" +
                "Với lý do : " + refuseReason +
                "\n" +
                "Chúng tôi hiểu rằng điều này có thể tạo ra bất tiện và thất vọng. Để đền bù, chúng tôi muốn đề xuất một số lựa chọn khác hoặc giúp đỡ để đảm bảo chuyến đi của Quý khách vẫn diễn ra thuận lợi nhất có thể. Xin vui lòng liên hệ với chúng tôi qua số điện thoại hoặc email dưới đây để chúng tôi có thể hỗ trợ Quý khách:\n" +
                "\n" +
                "0389718892\n" +
                "\n" +
                "Chúng tôi chân thành xin lỗi về sự bất tiện này và hy vọng Quý khách sẽ hiểu rằng quyết định này không phản ánh đến mong muốn phục vụ của chúng tôi. Xin cảm ơn Quý khách đã chọn Armani Hotel, và chúng tôi mong được phục vụ Quý khách trong tương lai.\n" +
                "\n" +
                "Trân trọng,\n" +
                "[Armani Hotel]\n";
        DataUtil.sendMailCommon(mailTo, subject, content, mailService);
        // set hóa đơn chi tiết
        for (OrderDetail orderDetail : order.getOrderDetailList()) {
            orderDetail.setStatus(Constant.ORDER_DETAIL.REFUSE);
            orderDetailRepository.save(orderDetail);
        }
        orderRepository.updateStatus(id, stt, refuseReason);
    }

    @Override
    public void cancel(String id, Integer stt) {
        orderRepository.updateStatus(id, stt);
        // set hóa đơn chi tiết
        orderDetailRepository.updateStatusByOrderId(id, stt);
    }

}
