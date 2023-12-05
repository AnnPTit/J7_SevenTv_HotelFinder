package com.example.demo.repository;

import com.example.demo.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, String> {

    @Query(value = "select * from favourite  where id_custom = :idCustom and  id_room =:idRoom ", nativeQuery = true)
    List<Favourite> findByIdCustomAndIdRoom(@Param("idCustom") String idCustom, @Param("idRoom") String idRoom);

}
