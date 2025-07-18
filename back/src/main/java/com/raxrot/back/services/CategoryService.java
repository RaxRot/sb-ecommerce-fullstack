package com.raxrot.back.services;

import com.raxrot.back.dtos.CategoryDTO;
import com.raxrot.back.dtos.CategoryResponse;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy,String sortOrder);
    CategoryDTO getCategoryById(Long categoryId);
    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);
    void deleteCategory(Long categoryId);
}
