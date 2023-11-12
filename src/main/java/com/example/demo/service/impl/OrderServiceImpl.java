package com.example.demo.service.impl;

import com.example.demo.constant.Constant;
import com.example.demo.dto.ConfirmOrderDTO;
import com.example.demo.dto.OrderDetailExport;
import com.example.demo.dto.OrderExportDTO;
import com.example.demo.dto.RevenueDTO;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderTimeline;
import com.example.demo.errors.BadRequestAlertException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderTimelineRepository;
import com.example.demo.service.OrderService;
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
    private CustomerRepository customerRepository;
    @Autowired
    private OrderTimelineRepository orderTimelineRepository;
    private NumToViet numToViet;

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
        if (customer1.getCitizenId().equals(Constant.citizenId) &&
                DataUtil.isNull(confirmOrderDTO.getCitizenId())) {
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
        if (customer1.getCitizenId().equals(Constant.citizenId) && customer1.getCitizenId().equals(confirmOrderDTO.getCitizenId())) {
            // kiểm tra -> không được trùng
            ConfirmOrderDTO confirmOrderDTO1 = new ConfirmOrderDTO();
            confirmOrderDTO1.setMessage("Căn cước công dân không được trùng nhau !");
            return confirmOrderDTO1;
        }
        // Cập nhật trạng thái order
        Order order = orderRepository.getById(confirmOrderDTO.getOrderId());
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
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        int year = zonedDateTime.getYear();
        int month = zonedDateTime.getMonthValue();
        int day = zonedDateTime.getDayOfMonth();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream employeeReportStream = getClass().getResourceAsStream("/templates/doc/recommended.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("logoPath", getClass().getResourceAsStream("/templates/image/logo.png"));
        parameters.put("code", orderExportDTO.getCode());
        parameters.put("deliverer", orderExportDTO.getCreater());
        parameters.put("customer", orderExportDTO.getCustomer());
        parameters.put("bookingDay", DataUtil.dateToString(orderExportDTO.getBookingDay()));
        parameters.put("checkin", DataUtil.dateToString(orderExportDTO.getCheckIn()));
        parameters.put("checkOut", DataUtil.dateToString(orderExportDTO.getCheckOut()));
        parameters.put("day", day);
        parameters.put("month", month);
        parameters.put("year", year);
        parameters.put("note", StringUtils.isNotBlank(orderExportDTO.getNote()) ? orderExportDTO.getNote() : "");
        long total = orderExportDTO.getTotalMoney().longValue();
        String totalPriceString = numToViet.num2String(total) + " đồng";
        totalPriceString = totalPriceString.substring(0, 1).toUpperCase() + totalPriceString.substring(1);
        //stringTotalPrice
        parameters.put("stringTotalPrice", totalPriceString);
        parameters.put("total", DataUtil.currencyFormat(orderExportDTO.getTotalMoney()));
        parameters.put("vat", DataUtil.currencyFormat(orderExportDTO.getVat()));
        parameters.put("dataTable", new JRBeanCollectionDataSource(dataTable));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JRDocxExporter export = new JRDocxExporter();
        export.setExporterInput(new SimpleExporterInput(jasperPrint));
        export.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        export.exportReport();
        return new ByteArrayResource(baos.toByteArray());
        // API thêm mới 1
        // API UPdate lan 2
    }

    @Override
    public List<RevenueDTO> getRevenue() {
        return orderRepository.getRevenue();
    }

    @Override
    public void refuse(String id, Integer stt) {
         orderRepository.updateStatus(id, stt);
    }

    @Override
    public void cancel(String id, Integer stt) {
         orderRepository.updateStatus(id, stt);
    }

}
