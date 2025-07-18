package com.raxrot.back.services.Impl;

import com.raxrot.back.configurations.AppConfig;
import com.raxrot.back.dtos.CategoryDTO;
import com.raxrot.back.dtos.CategoryResponse;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.Category;
import com.raxrot.back.repoitories.CategoryRepository;
import com.raxrot.back.services.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        log.info("Fetching paginated categories - page: {}, size: {}, sortBy: {}, sortOrder: {}",
                pageNumber, pageSize, sortBy, sortOrder);

        Sort sort = sortOrder.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryDTO> categoryDTOs = categoryPage.getContent()
                .stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse response = new CategoryResponse();
        response.setContent(categoryDTOs);
        response.setPageNumber(categoryPage.getNumber());
        response.setPageSize(categoryPage.getSize());
        response.setTotalElements(categoryPage.getTotalElements());
        response.setTotalPages(categoryPage.getTotalPages());
        response.setLastPage(categoryPage.isLast());

        log.info("Page {} of {} fetched. Total elements: {}. Last page: {}",
                response.getPageNumber(), response.getTotalPages(),
                response.getTotalElements(), response.isLastPage());

        return response;
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