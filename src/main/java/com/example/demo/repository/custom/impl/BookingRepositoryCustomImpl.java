package com.example.demo.repository.custom.impl;

import com.example.demo.dto.BookingRequest;
import com.example.demo.entity.Booking;
import com.example.demo.repository.custom.BookingRepositoryCustom;
import com.example.demo.util.DataUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingRepositoryCustomImpl implements BookingRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Booking> search(BookingRequest request, Pageable pageable) {
        Query queryCount = buildQuerySearch(request, null);

        Long count = Long.valueOf(queryCount.getResultList().size());

        List<Booking> result = new ArrayList<>();
        if (count > 0) {
            Query query = buildQuerySearch(request, pageable);
            result = query.getResultList();
        }
        return new PageImpl<>(result, pageable, count);
    }

    private Query buildQuerySearch(BookingRequest request, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder("select b.* from booking b inner join customer c ON b.id_customer = c.id where 1=1");

        if (!DataUtil.isNull(request.getKey())) {
            sql.append(" and ( c.fullname like :key or c.email  like :key or c.phone_number  like :key )  ");
            params.put("key", DataUtil.makeLikeStr(request.getKey()));
        }
        if (!DataUtil.isNull(request.getStatus())) {
            sql.append(" and b.status =:status ");
            params.put("status", request.getStatus());
        }

        sql.append(" order by b.update_at desc ");

        Query query = entityManager.createNativeQuery(sql.toString(), Booking.class);

        if (pageable != null) {
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()); // ví trí bản ghi đầu
            query.setMaxResults(pageable.getPageSize()); // giới hạn số lượng kết quả trả về trên mỗi trang
        }
        params.forEach(query::setParameter);
        return query;
    }


}
