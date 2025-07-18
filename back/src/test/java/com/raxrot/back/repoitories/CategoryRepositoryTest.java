package com.raxrot.back.repoitories;

import com.raxrot.back.models.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().name("Ketchup").build();
    }

    @Test
    @DisplayName("Create category")
    void createCategory() {
       Category savedCategory = categoryRepository.save(category);
        assertThat(savedCategory.getId()).isNotNull();
    }

    @Test
    @DisplayName("Get all categories")
    void getAllCategories() {
        Category category1 = Category.builder().name("Ketchup").build();
        Category category2 = Category.builder().name("Ketchup2").build();
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        List<Category> categories = categoryRepository.findAll();

        assertThat(categories).hasSize(2).extracting(Category::getName)
                .containsExactlyInAnyOrder("Ketchup", "Ketchup2");
    }

    @Test
    @DisplayName("Get category by id")
    void getCategoryById() {
        Category savedCategory = categoryRepository.save(category);
        Optional<Category> category = categoryRepository.findById(savedCategory.getId());

        assertThat(category.isPresent()).isTrue();
        assertThat(category.get().getName()).isEqualTo(savedCategory.getName());
    }

    @Test
    @DisplayName("Update category")
    void updateCategory() {
        Category savedCategory = categoryRepository.save(category);
        savedCategory.setName("Updated Ketchup");
        Category updated = categoryRepository.save(savedCategory);

        assertThat(updated.getName()).isEqualTo("Updated Ketchup");
    }

    @Test
    @DisplayName("Delete category")
    void deleteCategory() {
        Category savedCategory = categoryRepository.save(category);
        categoryRepository.delete(savedCategory);
        Optional<Category> category = categoryRepository.findById(savedCategory.getId());

        assertThat(category.isPresent()).isFalse();
        assertThat(categoryRepository.existsById(savedCategory.getId())).isFalse();
    }
}