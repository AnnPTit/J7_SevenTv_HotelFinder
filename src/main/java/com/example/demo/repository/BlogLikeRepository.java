package com.example.demo.repository;

import com.example.demo.entity.BlogLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogLikeRepository extends JpaRepository<BlogLike, String> {
}
