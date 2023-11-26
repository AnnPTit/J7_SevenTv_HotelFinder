package com.example.demo.repository;

import com.example.demo.entity.Blog;
import com.example.demo.entity.BlogLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogLikeRepository extends JpaRepository<BlogLike, String> {

    @Query(value = "select count(*)  from blog_like bl where bl.id_blog = :blogId and bl.status = 1", nativeQuery = true)
    Integer countLike(@Param("blogId") String blogId);

    @Query(value = "SELECT *\n" +
            "FROM blog_like bl\n" +
            "WHERE id_custom IS null and id_blog =:blogId", nativeQuery = true)
    List<BlogLike> anonymousLike(@Param("blogId") String blogId);


    @Query(value = "SELECT *\n" +
            "FROM blog_like bl\n" +
            "WHERE id_custom =:customId and id_blog =:blogId", nativeQuery = true)
    List<BlogLike> customLike(@Param("blogId") String blogId , @Param("customId") String customId);
}
