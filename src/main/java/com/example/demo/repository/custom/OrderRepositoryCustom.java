package com.example.demo.repository.custom;

import com.example.demo.dto.OrderDetailExport;
import com.example.demo.dto.OrderExportDTO;
import com.example.demo.dto.RevenueDTO;
import com.example.demo.dto.ServiceUsedInvoiceDTO;

import java.util.List;

public interface OrderRepositoryCustom {

    OrderExportDTO getData(String orderId);

    List<OrderDetailExport> getDataDetail(String orderId);

    List<RevenueDTO> getRevenue();

    List<ServiceUsedInvoiceDTO> getService(String orderId);

    List<ServiceUsedInvoiceDTO> getCombo(String orderId);

}
