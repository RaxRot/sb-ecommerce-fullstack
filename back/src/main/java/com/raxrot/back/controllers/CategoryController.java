package com.raxrot.back.controllers;

import com.raxrot.back.configurations.AppConfig;
import com.raxrot.back.dtos.CategoryDTO;
import com.raxrot.back.dtos.CategoryResponse;
import com.raxrot.back.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Category API", description = "CRUD operations for product categories")
public class CategoryController {

    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create new category", description = "Creates a new product category (admin only)")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        log.info("POST /public/categories - Creating category: {}", categoryDTO.getName());
        CategoryDTO createdDto = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @Operation(summary = "Get all categories", description = "Retrieves paginated list of all categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(value = "page", defaultValue = AppConfig.PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", defaultValue = AppConfig.PAGE_SIZE) Integer size,
            @RequestParam(value = "sortBy", defaultValue = AppConfig.SORT_BY_ID) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppConfig.SORT_ORDER_ASC) String sortOrder
    ) {
        log.info("GET /public/categories - page: {}, size: {}, sortBy: {}, sortOrder: {}", page, size, sortBy, sortOrder);
        CategoryResponse categoryResponse = categoryService.getAllCategories(page, size, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @Operation(summary = "Get category by ID", description = "Retrieves a single category by its ID")
    @ApiResponse(responseCode = "200", description = "Category found")
    @GetMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long categoryId) {
        log.info("GET /public/categories/{} - Fetching category by ID", categoryId);
        CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @Operation(summary = "Update category", description = "Updates an existing category (admin only)")
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId,
                                                      @Valid @RequestBody CategoryDTO categoryDTO) {
        log.info("PUT /public/categories/{} - Updating category with name: {}", categoryId, categoryDTO.getName());
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, categoryDTO);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @Operation(summary = "Delete category", description = "Deletes a category by its ID (admin only)")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        log.info("DELETE /admin/categories/{} - Deleting category", categoryId);
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}