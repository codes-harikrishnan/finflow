package com.harikrishnan.finflow.category.controller;

import com.harikrishnan.finflow.category.dto.CategoryRequest;
import com.harikrishnan.finflow.category.dto.CategoryResponse;
import com.harikrishnan.finflow.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryRequest categoryRequest){
        log.info("Received endpoint request to create a category");
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.addCategory(categoryRequest));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories () {
        log.info("Received endpoint request to get all categories");
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory (@PathVariable Long id) {
        log.info("Received endpoint request to delete category");
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
