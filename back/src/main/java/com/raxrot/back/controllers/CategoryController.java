package com.raxrot.back.controllers;

import com.raxrot.back.configurations.AppConfig;
import com.raxrot.back.dtos.CategoryDTO;
import com.raxrot.back.dtos.CategoryResponse;
import com.raxrot.back.services.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        log.info("POST /public/categories - Creating category: {}", categoryDTO.getName());
        CategoryDTO createdDto = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(value = "page", defaultValue = AppConfig.PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", defaultValue = AppConfig.PAGE_SIZE) Integer size,
            @RequestParam(value = "sortBy", defaultValue = AppConfig.SORT_BY_ID) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppConfig.SORT_ORDER_ASC) String sortOrder
    ) {
        log.info("GET /public/categories - page: {}, size: {}, sortBy: {}, sortOrder: {}", page, size, sortBy, sortOrder);

        CategoryResponse categoryResponse = categoryService.getAllCategories(page, size, sortBy, sortOrder);

        log.info("Returned {} categories on page {} of {}",
                categoryResponse.getContent().size(),
                categoryResponse.getPageNumber(),
                categoryResponse.getTotalPages());

        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long categoryId) {
        log.info("GET /public/categories/{} - Fetching category by ID", categoryId);
        CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId,
                                                      @Valid @RequestBody CategoryDTO categoryDTO) {
        log.info("PUT /public/categories/{} - Updating category with name: {}", categoryId, categoryDTO.getName());
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, categoryDTO);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        log.info("DELETE /admin/categories/{} - Deleting category", categoryId);
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
