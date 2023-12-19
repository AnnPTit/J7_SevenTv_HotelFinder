package com.example.demo.repository;

import com.example.demo.entity.Combo;
import com.example.demo.entity.ComboUsed;
import com.example.demo.entity.OrderDetail;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public interface ComboUsedRepository extends JpaRepository<ComboUsed, String> {

    @Query(value = "SELECT * FROM combo_used where order_detail_id = ?1 ORDER BY create_at DESC", nativeQuery = true)
    List<ComboUsed> getAllByOrderDetailId(String id);

    @Query(value = "SELECT cu FROM ComboUsed cu WHERE cu.combo.id=:combo AND cu.orderDetail.id=:orderDetail")
    ComboUsed getByCombo(String combo, String orderDetail);

    @Modifying()
    @Query(value = "UPDATE ComboUsed cu SET cu.quantity=:quantity WHERE cu.combo.id=:comboId")
    void updateQuantityComboUsed(@Param("quantity") Integer quantity, @Param("comboId") String comboId);

    @Modifying()
    @Query(value = "UPDATE ComboUsed cu SET cu.quantity=:quantity WHERE cu.id=:comboId")
    void updateQuantity(@Param("quantity") Integer quantity, @Param("comboId") String comboId);

}
