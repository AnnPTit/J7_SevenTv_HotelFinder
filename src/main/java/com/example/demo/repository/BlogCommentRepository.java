package com.example.demo.repository;

import com.example.demo.entity.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, String> {

    @Query(value = "select * from blog_comment bc where bc.id_blog =:blogId order by create_at desc ", nativeQuery = true)
    Page<BlogComment> getComment(@Param("blogId") String blogId, Pageable pageable);

}
