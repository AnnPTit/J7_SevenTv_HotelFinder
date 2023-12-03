package com.example.demo.repository.custom.impl;

import com.example.demo.dto.OrderDetailExport;
import com.example.demo.dto.OrderExportDTO;
import com.example.demo.dto.RevenueDTO;
import com.example.demo.dto.ServiceUsedInvoiceDTO;
import com.example.demo.repository.custom.OrderRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OrderExportDTO getData(String orderId) {
        String sql = "select\n" +
                " o.order_code as code,\n" +
                " o.create_by as creater,\n" +
                " c.fullname as customer ,\n" +
                " o.create_at as bookingDay,\n" +
                " o.booking_date_start as checkIn ,\n" +
                " o.booking_date_end as checkOut ,\n" +
                " o.note ,\n" +
                " coalesce(o.money_given_by_customer, 0) as monneyCustom ,\n" +
                " coalesce(o.deposit, 0) as deposit ,\n" +
                " coalesce(o.vat, 0) as vat ,\n" +
                " coalesce(o.total_money, 0) as totalMoney ,\n" +
                " coalesce(o.excess_money, 0) as excessMoney,\n" +
                " coalesce(o.surcharge , 0) as surcharge,\n" +
                " coalesce(o.discount , 0) as discount \n" +
                "from\n" +
                " `order` o\n" +
                "inner join account a on\n" +
                " a.id = o.account_id\n" +
                " and a.status = 1\n" +
                "inner join customer c on\n" +
                " c.id = o.customer_id\n" +
                " and c.status = 1\n" +
                "where\n" +
                " o.id =:orderId";
        Query query = entityManager.createNativeQuery(sql, "orderResult");
        query.setParameter("orderId", orderId);
        @SuppressWarnings("unchecked")
        List<OrderExportDTO> result = query.getResultList();
        return result.get(0);
    }

    @Override
    public List<OrderDetailExport> getDataDetail(String orderId) {
        String sql = "select\n" +
                " r.room_name as roomName ,\n" +
                " tr.type_room_name as typeRoom ,\n" +
                " coalesce(od.customer_quantity , 0) as quantity,\n" +
                " od.check_in_datetime as checkIn ,\n" +
                " od.check_out_datetime as checkOut ,\n" +
                " tr.price_per_day as unitPrice ,\n" +
                " (DATEDIFF(od.check_out_datetime, od.check_in_datetime)* tr.price_per_day ) as totalPrice\n" +
                "from\n" +
                " order_detail od\n" +
                "join room r on\n" +
                " r.id = od.room_id\n" +
                "join type_room tr on\n" +
                " tr.id = r.type_room_id\n" +
                "where\n" +
                " od.order_id =:orderId";
        Query query = entityManager.createNativeQuery(sql, "orderDetailResult");
        query.setParameter("orderId", orderId);
        @SuppressWarnings("unchecked")
        List<OrderDetailExport> result = query.getResultList();
        return result;
    }

    @Override
    public List<RevenueDTO> getRevenue() {
        String sql = "SELECT EXTRACT(MONTH FROM update_at) AS month, EXTRACT(YEAR FROM update_at) AS year, SUM(total_money) AS revenue\n" +
                "FROM `order` WHERE status = 3 GROUP BY EXTRACT(YEAR FROM update_at), EXTRACT(MONTH FROM update_at)\n" +
                "ORDER BY EXTRACT(YEAR FROM update_at), EXTRACT(MONTH FROM update_at)";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> rows = query.getResultList();

        List<RevenueDTO> resultList = new ArrayList<>();
        for (Object[] row : rows) {
            RevenueDTO dto = new RevenueDTO();
            dto.setMonth((Integer) row[0]);
            dto.setYear((Integer) row[1]);
            dto.setRevenue((BigDecimal) row[2]);
            resultList.add(dto);
        }
        return resultList;
    }

    @Override
    public List<ServiceUsedInvoiceDTO> getService(String orderId) {
        String sql = "select \n" +
                "r.room_name as roomName1 ,\n" +
                "s.service_name as service ,\n" +
                "su.quantity as quantity2,\n" +
                "s.price ,\n" +
                "(su.quantity* s.price) as total2 ,\n" +
                " su.create_at as createAt\n" +
                "from order_detail od \n" +
                "inner join room r on r.id = od.room_id \n" +
                "inner join `order` o on o.id = od.order_id \n" +
                "inner join service_used su on su.order_detail_id = od.id \n" +
                "left join service s on s.id = su.service_id \n" +
                "where o.id =:orderId";
        Query query = entityManager.createNativeQuery(sql, "serviceResult");
        query.setParameter("orderId", orderId);
        @SuppressWarnings("unchecked")
        List<ServiceUsedInvoiceDTO> result = query.getResultList();
        return result;
    }

    @Override
    public List<ServiceUsedInvoiceDTO> getCombo(String orderId) {
        String sql = "select\n" +
                " r.room_name as roomName1 ,\n" +
                " c.combo_name as service ,\n" +
                " cu.quantity as quantity2,\n" +
                " c.price ,\n" +
                " (cu.quantity * c.price) as total2, \n" +
                " cu.create_at as createAt\n" +
                "from\n" +
                " order_detail od\n" +
                "inner join room r on\n" +
                " r.id = od.room_id\n" +
                "inner join `order` o on\n" +
                " o.id = od.order_id\n" +
                "inner join combo_used cu on\n" +
                " cu.order_detail_id = od.id\n" +
                "left join combo c on\n" +
                " c.id = cu.combo_id\n" +
                "where\n" +
                " o.id =:orderId";
        Query query = entityManager.createNativeQuery(sql, "serviceResult");
        query.setParameter("orderId", orderId);
        @SuppressWarnings("unchecked")
        List<ServiceUsedInvoiceDTO> result = query.getResultList();
        return result;
    }

}
