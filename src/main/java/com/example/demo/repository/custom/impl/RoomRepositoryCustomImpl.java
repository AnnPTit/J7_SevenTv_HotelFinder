package com.example.demo.repository.custom.impl;

import com.example.demo.dto.*;
import com.example.demo.entity.Photo;
import com.example.demo.entity.Room;
import com.example.demo.repository.PhotoRepository;
import com.example.demo.repository.custom.RoomRepositoryCustom;
import com.example.demo.util.DataUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomRepositoryCustomImpl implements RoomRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PhotoRepository photoRepository;

    private static final char KEY_ESCAPE = '\\';

    @Override
    public Page<RoomResponeDTO> search(RoomRequestDTO request, Pageable pageable) {
        Query queryCount = buildQuerySearch(request, null);

        Long count = Long.valueOf(queryCount.getResultList().size());

        List<RoomResponeDTO> result = new ArrayList<>();
        if (count > 0) {
            Query query = buildQuerySearch(request, pageable);
            result = query.getResultList();
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
                        " r.id as roomId,\n" +
                        " r.room_name as roomName,\n" +
                        " tr.type_room_name as typeRoom,\n" +
                        " o.booking_date_start as bookingStart,\n" +
                        " o.booking_date_end as bookingEnd,\n" +
                        " tr.price_per_day as price,\n" +
                        " tr.capacity  as capacity,\n" +
                        " tr.children  as children,\n" +
                        " od.customer_quantity as numberCustom,\n" +
                        " o.status as orderStatus,\n" +
                        " o.create_at as bookingDay,\n" +
                        " MAX(p.url) as url,\n" +
                        " o.order_code as orderCode,\n" +
                        " o.deposit,\n" +
                        " o.refuse_reason as refuseReason\n" +
                        "from\n" +
                        " `order` o\n" +
                        "inner join order_detail od on\n" +
                        " o.id = od.order_id\n" +
                        "inner join room r on\n" +
                        " od.room_id = r.id\n" +
                        "inner join type_room tr on\n" +
                        " tr.id = r.type_room_id\n" +
                        " and tr.status = 1\n" +
                        "left join photo p on\n" +
                        " p.room_id = r.id\n" +
                        "where\n" +
                        " o.customer_id = :customId\n" +
                        " and o.status = :odStt\n" +
                        " and o.type_of_order = 0\n" +
                        "group by\n" +
                        " r.id,\n" +
                        " r.room_name,\n" +
                        " tr.type_room_name,\n" +
                        " o.booking_date_start,\n" +
                        " o.booking_date_end,\n" +
                        " tr.price_per_day,\n" +
                        " od.customer_quantity,\n" +
                        " o.status,\n" +
                        " o.create_at,\n" +
                        " o.order_code \n" +
                        "order by\n" +
                        " o.create_at desc";

        Query query = entityManager.createNativeQuery(sql, "cartResult");
        query.setParameter("customId", customId);
        query.setParameter("odStt", odStt);
        @SuppressWarnings("unchecked")
        List<CartDTO> result = query.getResultList();
        return result;
    }

    @Override
    public Page<RoomCardDTO> searchRoom(FacilityRequestDTO request, Pageable pageable) {
        Query queryCount = buildQuerySearchRoom(request, null);

        Long count = Long.valueOf(queryCount.getResultList().size());

        List<RoomCardDTO> result = new ArrayList<>();
        for (RoomCardDTO roomCardDTO : result) {
            List<Photo> photos = photoRepository.getPhotoByRoomId(roomCardDTO.getId());
            if (photos.size() != 0) {
                String url = photos.get(0).getUrl();
                roomCardDTO.setUrl(url);
            }
        }
        if (count > 0) {
            Query query = buildQuerySearchRoom(request, pageable);
            result = query.getResultList();
            for (RoomCardDTO roomCardDTO : result) {
                List<Photo> photos = photoRepository.getPhotoByRoomId(roomCardDTO.getId());
                if (photos.size() != 0) {
                    String url = photos.get(0).getUrl();
                    roomCardDTO.setUrl(url);
                }
            }
        }
        return new PageImpl<>(result, pageable, count);
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
                        "tr.children ,\n" +
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
                    " ((od.check_in_datetime NOT BETWEEN :checkIn AND :checkOut) or o.status in(0,3,6,7,8)) \n" +
                    " AND \n" +
                    " ((od.check_out_datetime NOT BETWEEN :checkIn AND :checkOut) or o.status in(0,3,6,7,8))\n" +
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
        if (request.getChildren() !=0) {
            sql.append(" and (tr.children =:children )");
            params.put("children", request.getChildren());
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
                        "    tr.children,\n" +
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


    private Query buildQuerySearchRoom(FacilityRequestDTO request, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder("select\n" +
                "  r.id ,\n" +
                "  r.room_name as name , \n" +
                "  tr.type_room_name as typeRoom,\n" +
                "  tr.capacity ,\n" +
                "  tr.children ,\n" +
                "  COUNT(od.id) as bookingCount , \n" +
                "  tr.price_per_day as price\n" +
                "from\n" +
                "  room r\n" +
                "inner join type_room tr\n" +
                "                on\n" +
                "  tr.id = r.type_room_id\n" +
                "  and tr.status = 1\n" +
                "left join room_facility rf on\n" +
                "  rf.room_id = r.id\n" +
                "left join order_detail od on\n" +
                "  od.room_id = r.id\n" +
                "where\n" +
                "  1 = 1");
        if (request.getRoomname() != null && !("").equals(request.getRoomname())) {
            sql.append(" and r.room_name like :roomName");
            params.put("roomName", DataUtil.makeLikeStr(request.getRoomname()));
        }
        if (request.getSelectedChildren() != null && request.getSelectedChildren() != 0) {
            sql.append(" and tr.children = :children");
            params.put("children", request.getSelectedChildren());
        }
        if (!CollectionUtils.isEmpty(request.getPriceRange()) && !request.getPriceRange().contains(null)) {
            sql.append(" and (tr.price_per_day between :start and :end)");
            params.put("start", request.getPriceRange().get(0));
            params.put("end", request.getPriceRange().get(1));
        }
        if (!CollectionUtils.isEmpty(request.getSelectedFacilities()) && !request.getPriceRange().contains(null)) {
            sql.append(" and rf.facility_id in :facility");
            List<String> ids = request.getSelectedFacilities().stream().map(item -> item.getId()).collect(Collectors.toList());
            params.put("facility", ids);
        }
        if (request.getTypeRoomChose() != null && !("").equals(request.getTypeRoomChose())) {
            sql.append(" and tr.type_room_code =:code ");
            params.put("code", request.getTypeRoomChose());
        }
        sql.append(" group by r.id ");
        if (request.getIsCrease() != null) {
            sql.append(" order by tr.price_per_day  ");
            if (request.getIsCrease().equals(false)) {
                sql.append(" desc ");
            } else {
                sql.append(" asc ");
            }
        }

        if (request.getIsCreaseBook() != null) {
            sql.append(" order by COUNT(od.id) ");
            if (request.getIsCreaseBook().equals(false)) {
                sql.append(" desc ");
            } else {
                sql.append(" asc ");
            }
        }

        Query query = entityManager.createNativeQuery(sql.toString(), "RoomCardResult"); // Chỉ định lớp mục tiêu là Room

        if (pageable != null) {
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()); // ví trí bản ghi đầu
            query.setMaxResults(pageable.getPageSize()); // giới hạn số lượng kết quả trả về trên mỗi trang
        }
        params.forEach(query::setParameter);
        return query;
    }


}
