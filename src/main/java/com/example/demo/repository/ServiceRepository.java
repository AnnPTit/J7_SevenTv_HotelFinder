package com.example.demo.repository;

import com.example.demo.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

    @Query(value = "select * from service where status =1", nativeQuery = true)
    Page<Service> findAll(Pageable pageable);


    @Query(value = "SELECT *\n" +
            "FROM service\n" +
            "WHERE status = 1\n" +
            "  AND ((:serviceCode IS NULL OR service_code = :serviceCode)\n" +
            "       OR (:serviceName IS NULL OR service_name LIKE CONCAT('%', :serviceName, '%')))\n" +
            "  AND (:serviceTypeId IS NULL OR service_type_id = :serviceTypeId)\n" +
            "  AND (:unitId IS NULL OR unit_id = :unitId)", nativeQuery = true)
    Page<Service> loadAndSearch(@Param("serviceCode") String serviceCode,
                                @Param("serviceName") String serviceName,
                                @Param("serviceTypeId") String serviceTypeId,
                                @Param("unitId") String unitId,
                                Pageable pageable);

    boolean existsByServiceCode(String code);

}
