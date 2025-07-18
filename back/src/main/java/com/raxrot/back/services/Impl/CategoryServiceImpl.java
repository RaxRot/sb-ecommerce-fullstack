package com.raxrot.back.services.Impl;

import com.raxrot.back.configurations.AppConfig;
import com.raxrot.back.dtos.CategoryDTO;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.Category;
import com.raxrot.back.repoitories.CategoryRepository;
import com.raxrot.back.services.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.info("Creating new category: {}", categoryDTO.getName());
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created with ID: {}", savedCategory.getId());
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        log.info("Fetched {} categories", categories.size());
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        log.info("Fetching category with ID: {}", categoryId);
        Category category = getCategory(categoryId);
        log.info("Category found: {}", category.getName());
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        log.info("Updating category with ID: {}", categoryId);
        Category category = getCategory(categoryId);
        category.setName(categoryDTO.getName());
        Category savedCategory = categoryRepository.save(category);
        log.info("Category updated: {}", savedCategory.getName());
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        log.info("Deleting category with ID: {}", categoryId);
        Category category = getCategory(categoryId);
        categoryRepository.delete(category);
        log.info("Category deleted with ID: {}", categoryId);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category with ID {} not found", categoryId);
                    return new ApiException(AppConfig.NO_CATEGORY_FOUND);
                });
    }
}