package com.example.demo.repository;

import com.example.demo.entity.Combo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface ComboRepository extends JpaRepository<Combo, String> {
    @Query(value = "select *\n" +
            "from combo where status =1 order by create_at", nativeQuery = true)
    Page<Combo> findAll(Pageable pageable);

    boolean existsByComboCode(String code);

    @Query(value = "SELECT DISTINCT\n" +
            "    c.id,\n" +
            "    c.combo_code,\n" +
            "    c.combo_name,\n" +
            "    c.price,\n" +
            "    c.note,\n" +
            "    c.create_at,\n" +
            "    c.create_by,\n" +
            "    c.update_at,\n" +
            "    c.updated_by,\n" +
            "    c.deleted,\n" +
            "    c.status\n" +
            "FROM Combo c\n" +
            "         JOIN combo_service cs ON c.id = cs.combo_id\n" +
            "         JOIN service s ON s.id = cs.service_id\n" +
            "WHERE c.status = 1\n" +
            "  AND ((:comboCode IS NULL OR c.combo_code = :comboCode)\n" +
            "    AND (:comboName IS NULL OR c.combo_name LIKE CONCAT('%', :comboName, '%')))\n" +
            "  AND (:serviceId IS NULL OR s.id = :serviceId)\n" +
            "  AND c.price BETWEEN :start AND :end\n" +
            "ORDER BY c.create_at DESC\n" +
            "LIMIT :pageSize\n" +
            "    OFFSET :offset", nativeQuery = true)
    List<Combo> searchCombosWithService(String comboCode, String comboName, String serviceId, BigDecimal start, BigDecimal end, int pageSize, int offset);

    @Query(value = "SELECT COUNT(DISTINCT c.id) FROM Combo c " +
            "JOIN combo_service cs ON c.id = cs.combo_id " +
            "JOIN service s ON s.id = cs.service_id " +
            "WHERE c.status = 1 " +
            "AND ((:comboCode IS NULL OR c.combo_code = :comboCode) " +
            "AND (:comboName IS NULL OR c.combo_name LIKE CONCAT('%', :comboName, '%'))) " +
            "AND (:serviceId IS NULL OR s.id = :serviceId) " +
            "AND c.price BETWEEN :start AND :end",
            nativeQuery = true)
    long countSearch(String comboCode, String comboName, String serviceId, BigDecimal start, BigDecimal end);


}
