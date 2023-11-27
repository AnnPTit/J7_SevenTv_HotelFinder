package com.example.demo.repository;

import com.example.demo.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, String> {

    @Query(value = "update blog b set b.count_like = :countLike where b.id = :blogId ", nativeQuery = true)
    void updateLike(@Param("blogId") String blogId, @Param("countLike") Integer countLike);

    @Query(value = "update blog b set b.count_view  = :countView where b.id = :blogId", nativeQuery = true)
    void updateView(@Param("blogId") String blogId, @Param("countView") Integer countView);

    @Query(value = "select * from blog where id =:id and status =1 ", nativeQuery = true)
    Blog getOne(@Param("id") String id);

    @Query(value = "SELECT *\n" +
            "FROM blog\n" +
            "WHERE (:title is null or title LIKE CONCAT('%', :title ,'%'))\n" +
            " and status = 1 order by update_at desc", nativeQuery = true)
    Page<Blog> loadAndSearch(@Param("title") String title, Pageable pageable);
}
