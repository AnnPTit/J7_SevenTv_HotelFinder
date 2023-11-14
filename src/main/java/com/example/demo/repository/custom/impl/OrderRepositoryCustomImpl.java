package com.example.demo.repository.custom.impl;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.OrderDetailExport;
import com.example.demo.dto.OrderExportDTO;
import com.example.demo.dto.RevenueDTO;
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
        String sql ="select \n" +
                "o.order_code  as code, \n" +
                "o.create_by as creater, \n" +
                "c.fullname as customer ,\n" +
                "o.create_at as bookingDay,\n" +
                "o.booking_date_start as checkIn ,\n" +
                "o.booking_date_end as checkOut ,\n" +
                "o.note ,\n" +
                "o.money_given_by_customer as monneyCustom , \n" +
                "o.deposit as deposit ,\n" +
                "o.vat as vat ,\n" +
                "o.total_money as totalMoney ,\n" +
                "o.excess_money as excessMoney\n" +
                "from `order` o \n" +
                "inner join account a on a.id =o.account_id and a.status =1 \n" +
                "inner join customer c on c.id  = o.customer_id and c.status =1\n" +
                "where o.id =:orderId";
        Query query = entityManager.createNativeQuery(sql, "orderResult");
        query.setParameter("orderId", orderId);
        @SuppressWarnings("unchecked")
        List<OrderExportDTO> result = query.getResultList();
        return result.get(0);
    }

    @Override
    public List<OrderDetailExport> getDataDetail(String orderId) {
        String sql ="select\n" +
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

}
