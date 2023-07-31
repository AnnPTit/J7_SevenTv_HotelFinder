package com.example.demo.repository;


import com.example.demo.entity.ComboService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface ComboServiceRepository extends JpaRepository<ComboService, String> {
    @Query(value = "select *\n" +
            "from combo_service where status =1 order by create_at", nativeQuery = true)
    Page<ComboService> findAll(Pageable pageable);


    @Query(value = "select  * from combo_service where combo_id =?1 and service_id =?2 and status=1", nativeQuery = true)
    ComboService findByComboAndService(String comboID, String serviceID);

    @Modifying
    @Transactional
    @Query(value = "delete\n" +
            "from combo_service\n" +
            "where combo_id = ?1" +
            "  and service_id = ?2", nativeQuery = true)
    void deteleComboService(String comboId, String serviceID);
}
