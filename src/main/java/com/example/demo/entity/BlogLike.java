package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "blog_like")
public class BlogLike {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_custom", nullable = false)
    private Customer customer;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_blog", nullable = false)
    private Blog blog;

    @Column(name = "status")
    private Integer status;
}
