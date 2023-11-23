package com.example.demo.controller;

import com.example.demo.dto.BlogDTO;
import com.example.demo.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;


    @GetMapping("/load")
    public Page<BlogDTO> load(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return blogService.getPaginate(pageable);
    }


}
