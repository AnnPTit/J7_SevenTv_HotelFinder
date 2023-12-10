package com.example.demo.repository.custom.impl;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.RoomRequestDTO;
import com.example.demo.dto.RoomResponeDTO;
import com.example.demo.repository.custom.RoomRepositoryCustom;
import com.example.demo.util.DataUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomRepositoryCustomImpl implements RoomRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private static final char KEY_ESCAPE = '\\';

    @Override
    public Page<RoomResponeDTO> search(RoomRequestDTO request, Pageable pageable) {
        Query queryCount = buildQuerySearch(request, null);

        Long count = Long.valueOf(queryCount.getResultList().size());

        List<RoomResponeDTO> result = new ArrayList<>();
        if (count > 0) {
            Query query = buildQuerySearch(request, pageable);
            result = query.getResultList(); // Sử dụng getResultList() thay vì getSingleResult()
        }
        return new PageImpl<>(result, pageable, count);
    }

    @Override
    public Page<RoomResponeDTO> topBook(Pageable pageable) {
        Query queryCount = buildQuerySearch(null);

        Long count = Long.valueOf(queryCount.getResultList().size());

        List<RoomResponeDTO> result = new ArrayList<>();
        if (count > 0) {
            Query query = buildQuerySearch(pageable);
            result = query.getResultList(); // Sử dụng getResultList() thay vì getSingleResult()
        }
        return new PageImpl<>(result, pageable, count);
    }

    @Override
    public List<CartDTO> getCart(String customId, Integer odStt) {
        String sql =
                "select\n" +
                        "  r.id as roomId,\n" +
                        "  r.room_name as roomName,\n" +
                        "  tr.type_room_name as typeRoom,\n" +
                        "  o.booking_date_start as bookingStart,\n" +
                        "  o.booking_date_end as bookingEnd,\n" +
                        "  tr.price_per_day as price,\n" +
                        "  od.customer_quantity as numberCustom,\n" +
                        "  o.status as orderStatus,\n" +
                        "  o.create_at as bookingDay,\n" +
                        "  MAX(p.url) as url,\n" +
                        "  o.order_code as orderCode,\n" +
                        "  o.deposit,\n" +
                        "  o.refuse_reason as refuseReason\n" +
                        "from\n" +
                        "  `order` o\n" +
                        "inner join order_detail od on\n" +
                        "  o.id = od.order_id\n" +
                        "inner join room r on\n" +
                        "  od.room_id = r.id\n" +
                        "inner join type_room tr on\n" +
                        "  tr.id = r.type_room_id\n" +
                        "  and tr.status = 1\n" +
                        "left join photo p on\n" +
                        "  p.room_id = r.id\n" +
                        "where\n" +
                        "  o.customer_id = :customId\n" +
                        "  and o.status = :odStt\n" +
                        "group by\n" +
                        "  r.id,\n" +
                        "  r.room_name,\n" +
                        "  tr.type_room_name,\n" +
                        "  o.booking_date_start,\n" +
                        "  o.booking_date_end,\n" +
                        "  tr.price_per_day,\n" +
                        "  od.customer_quantity,\n" +
                        "  o.status,\n" +
                        "  o.create_at,\n" +
                        "  o.order_code";

        Query query = entityManager.createNativeQuery(sql, "cartResult");
        query.setParameter("customId", customId);
        query.setParameter("odStt", odStt);
        @SuppressWarnings("unchecked")
        List<CartDTO> result = query.getResultList();
        return result;
    }

    private Query buildQuerySearch(RoomRequestDTO request, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder(
                "select distinct r.id,\n" +
                        "r.room_code as roomCode\n" +
                        ",r.room_name as roomName\n" +
                        ", r.note , \n" +
                        "tr.type_room_name  as typeRoom ,\n" +
                        "tr.capacity ,\n" +
                        "tr.price_per_hours as pricePerHours ,\n" +
                        "tr.price_per_day  as pricePerDay, \n" +
                        "0 AS countBook\n" +
                        "from room r   \n" +
                        "inner join type_room tr on tr.id = r.type_room_id  \n" +
                        "left join order_detail od on od.room_id = r.id \n " +
                        "left join `order` o on o.id = od.order_id \n" +
                        "where r.status <> 0 " +
                        " and tr.status =1 \n "
        );

        if (request.getTypeRoom() != null && !("").equals(request.getTypeRoom())) {
            sql.append(" and tr.type_room_code = :typeRoom ");
            params.put("typeRoom", request.getTypeRoom());
        }
        if (request.getRoomName() != null && !("").equals(request.getRoomName())) {
            sql.append(" and r.room_name like :value");
            String value = DataUtil.likeSpecialToStr(request.getRoomName());
            params.put("value", DataUtil.makeLikeStr(value));
        }
        if (request.getCheckIn() != null && request.getCheckOut() != null) {
            sql.append(" AND (\n" +
                    " od.room_id IS NULL OR\n" +
                    " (\n" +
                    " ((od.check_in_datetime NOT BETWEEN :checkIn AND :checkOut) or o.status in(0,3,6,7)) \n" +
                    " AND \n" +
                    " ((od.check_out_datetime NOT BETWEEN :checkIn AND :checkOut) or o.status in(0,3,6,7))\n" +
                    " )\n" +
                    " ) ");
            params.put("checkIn", DataUtil.toLocalDateTime(request.getCheckIn()));
            params.put("checkOut", DataUtil.toLocalDateTime(request.getCheckOut()));
        }
        if (request.getNumberCustom() != 0) {
            sql.append(" and (tr.capacity = :capacity)");
            params.put("capacity", request.getNumberCustom());
        }
        if (!CollectionUtils.isEmpty(request.getPricePerDays()) && !request.getPricePerDays().contains(null)) {
            sql.append(" and (tr.price_per_day between :start and :end)");
            params.put("start", request.getPricePerDays().get(0));
            params.put("end", request.getPricePerDays().get(1));
        }

        Query query = entityManager.createNativeQuery(sql.toString(), "roomResult");

        if (pageable != null) {
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()); // ví trí bản ghi đầu
            query.setMaxResults(pageable.getPageSize()); // giới hạn số lượng kết quả trả về trên mỗi trang
        }

        params.forEach(query::setParameter);
        return query;
    }

    private Query buildQuerySearch(Pageable pageable) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder(
                "SELECT\n" +
                        "    r.id,\n" +
                        "    r.room_code AS roomCode,\n" +
                        "    r.room_name AS roomName,\n" +
                        "    r.note,\n" +
                        "    tr.type_room_name AS typeRoom,\n" +
                        "    tr.capacity,\n" +
                        "    tr.price_per_hours AS pricePerHours,\n" +
                        "    tr.price_per_day AS pricePerDay,\n" +
                        "    COUNT(od.id) AS countBook\n" +
                        "FROM\n" +
                        "    room r\n" +
                        "INNER JOIN type_room tr ON\n" +
                        "    tr.id = r.type_room_id\n" +
                        "LEFT JOIN order_detail od ON\n" +
                        "    od.room_id = r.id\n" +
                        "LEFT JOIN `order` o ON\n" +
                        "    o.id = od.order_id\n" +
                        "WHERE\n" +
                        "    r.status <> 0\n" +
                        "    AND tr.status = 1\n" +
                        "GROUP BY\n" +
                        "    r.id,\n" +
                        "    r.room_code,\n" +
                        "    r.room_name,\n" +
                        "    r.note,\n" +
                        "    tr.type_room_name,\n" +
                        "    tr.capacity,\n" +
                        "    tr.price_per_hours,\n" +
                        "    tr.price_per_day\n" +
                        "ORDER BY\n" +
                        "    countBook DESC\n"
        );
        Query query = entityManager.createNativeQuery(sql.toString(), "roomResult");

        if (pageable != null) {
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()); // ví trí bản ghi đầu
            query.setMaxResults(pageable.getPageSize()); // giới hạn số lượng kết quả trả về trên mỗi trang
        }
        params.forEach(query::setParameter);
        return query;
    }


}
