package com.example.demo.repository;

import com.example.demo.entity.ComboUsed;
import com.example.demo.entity.ServiceUsed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ServiceUsedRepository extends JpaRepository<ServiceUsed, String> {

    @Query(value = "SELECT * FROM service_used where order_detail_id = ?1 ORDER BY create_at DESC", nativeQuery = true)
    List<ServiceUsed> getAllByOrderDetailId(String id);

    @Query(value = "SELECT su FROM ServiceUsed su WHERE su.service.id=:service AND su.orderDetail.id=:orderDetail")
    ServiceUsed getByService(String service, String orderDetail);

    @Modifying()
    @Query(value = "UPDATE ServiceUsed su SET su.quantity=:quantity WHERE su.service.id=:serviceId")
    void updateQuantityServiceUsed(@Param("quantity") Integer quantity, @Param("serviceId") String serviceId);

}
