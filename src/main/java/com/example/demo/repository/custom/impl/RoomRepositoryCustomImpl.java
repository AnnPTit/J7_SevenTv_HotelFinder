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
    public List<CartDTO> getCart(String customId, Integer odStt) {
        String sql =
                "                       select\n" +
                        "\tr.id as roomId,\n" +
                        "\tr.room_name as roomName,\n" +
                        "\ttr.type_room_name as typeRoom,\n" +
                        "\to.booking_date_start as bookingStart,\n" +
                        "\to.booking_date_end as bookingEnd,\n" +
                        "\ttr.price_per_day as price,\n" +
                        "\tod.customer_quantity as numberCustom,\n" +
                        "\to.status as orderStatus,\n" +
                        "\to.create_at as bookingDay,\n" +
                        "\tMAX(p.url) as url,\n" +
                        "\to.order_code\n" +
                        "from\n" +
                        "\t`order` o\n" +
                        "inner join order_detail od on\n" +
                        "\to.id = od.order_id\n" +
                        "inner join room r on\n" +
                        "\tod.room_id = r.id\n" +
                        "inner join type_room tr on\n" +
                        "\ttr.id = r.type_room_id\n" +
                        "\tand tr.status = 1\n" +
                        "left join photo p on\n" +
                        "\tp.room_id = r.id\n" +
                        "where\n" +
                        "\to.customer_id = :customId\n" +
                        "\tand o.status = :odStt\n" +
                        "group by\n" +
                        "\tr.id,\n" +
                        "\tr.room_name,\n" +
                        "\ttr.type_room_name,\n" +
                        "\to.booking_date_start,\n" +
                        "\to.booking_date_end,\n" +
                        "\ttr.price_per_day,\n" +
                        "\tod.customer_quantity,\n" +
                        "\to.status,\n" +
                        "\to.create_at,\n" +
                        "\to.order_code";

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
                        "tr.price_per_day  as pricePerDay\n" +
                        "from room r   \n" +
                        "inner join type_room tr on tr.id = r.type_room_id  \n" +
                        "left join order_detail od on od.room_id - r.id  \n" +
                        "where r.status = 1\n" +
                        "and tr.status =1 \n "
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
            sql.append(" and (od.check_in_datetime not between :checkIn and :checkOut)\n" +
                    " and (od.check_out_datetime  not between :checkIn and :checkOut) ");
            params.put("checkIn", request.getCheckIn());
            params.put("checkOut", request.getCheckOut());
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


}
