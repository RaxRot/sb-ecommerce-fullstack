package com.raxrot.back.services.Impl;

import com.raxrot.back.dtos.CategoryDTO;
import com.raxrot.back.dtos.CategoryResponse;
import com.raxrot.back.models.Category;
import com.raxrot.back.repoitories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private ModelMapper modelMapper;
    private Category category;
    private CategoryDTO categoryDTO;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Category 1").build();
        categoryDTO=CategoryDTO.builder().id(1L).name("Category 1").build();
    }

    @Test
    @DisplayName("Create category")
    void createCategory() {
        //given
        BDDMockito.given(modelMapper.map(categoryDTO, Category.class)).willReturn(category);
        BDDMockito.given(modelMapper.map(category, CategoryDTO.class)).willReturn(categoryDTO);
        BDDMockito.given(categoryRepository.save(category)).willReturn(category);

        //when
        CategoryDTO savedCategory = categoryService.createCategory(categoryDTO);

        //then
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getId()).isEqualTo(categoryDTO.getId());
        assertThat(savedCategory.getName()).isEqualTo(categoryDTO.getName());
        assertThat(savedCategory).isEqualTo(categoryDTO);
    }

    @Test
    @DisplayName("Get all categories with pagination and sorting")
    void getAllCategories() {
        // given
        int pageNumber = 0;
        int pageSize = 2;
        String sortBy = "name";
        String sortOrder = "asc";

        List<Category> categoryList = List.of(
                Category.builder().id(1L).name("Category A").build(),
                Category.builder().id(2L).name("Category B").build()
        );

        List<CategoryDTO> categoryDTOList = List.of(
                CategoryDTO.builder().id(1L).name("Category A").build(),
                CategoryDTO.builder().id(2L).name("Category B").build()
        );

        Page<Category> categoryPage = new PageImpl<>(categoryList,
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending()), 2);

        BDDMockito.given(categoryRepository.findAll(Mockito.any(Pageable.class)))
                .willReturn(categoryPage);
        BDDMockito.given(modelMapper.map(categoryList.get(0), CategoryDTO.class))
                .willReturn(categoryDTOList.get(0));
        BDDMockito.given(modelMapper.map(categoryList.get(1), CategoryDTO.class))
                .willReturn(categoryDTOList.get(1));

        // when
        CategoryResponse response = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Category A");
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getPageSize()).isEqualTo(2);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.isLastPage()).isTrue();
    }

    @Test
    @DisplayName("Get category by id")
    void getCategoryById() {
        //given
        BDDMockito.given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        BDDMockito.given(modelMapper.map(category, CategoryDTO.class)).willReturn(categoryDTO);

        //when
        CategoryDTO fetchedCategory = categoryService.getCategoryById(1L);

        //then
        assertThat(fetchedCategory).isNotNull();
        assertThat(fetchedCategory.getId()).isEqualTo(categoryDTO.getId());
        assertThat(fetchedCategory.getName()).isEqualTo(categoryDTO.getName());
    }

    @Test
    @DisplayName("Update category")
    void updateCategory() {
        //given
        Category existingCategory = Category.builder().id(1L).name("Category 1").build();
        CategoryDTO updatedCategoryDTO = CategoryDTO.builder().id(1L).name("Category 2").build();
        Category updatedCategory = Category.builder().id(1L).name("Category 2").build();

        BDDMockito.given(categoryRepository.findById(1L)).willReturn(Optional.of(existingCategory));
        BDDMockito.given(categoryRepository.save(existingCategory)).willReturn(updatedCategory);
        BDDMockito.given(modelMapper.map(updatedCategory, CategoryDTO.class)).willReturn(updatedCategoryDTO);

        //when
        CategoryDTO savedCategoryDTO = categoryService.updateCategory(1L, updatedCategoryDTO);

        //then
        assertThat(savedCategoryDTO).isNotNull();
        assertThat(savedCategoryDTO.getId()).isEqualTo(updatedCategoryDTO.getId());
        assertThat(savedCategoryDTO.getName()).isEqualTo(updatedCategoryDTO.getName());
    }

    @Test
    @DisplayName("Delete category")
    void deleteCategory() {
        //given
        BDDMockito.given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        BDDMockito.willDoNothing().given(categoryRepository).delete(category);

        //when
        categoryService.deleteCategory(1L);

        //then
        BDDMockito.then(categoryRepository).should().delete(category);
    }
}