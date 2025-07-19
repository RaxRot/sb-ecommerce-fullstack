package com.raxrot.back.repoitories;

import com.raxrot.back.models.Category;
import com.raxrot.back.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().name("Category").build();
        categoryRepository.save(category);
        product = Product.builder().name("Milk").description("Milk description")
                .quantity(1).price(new BigDecimal(100)).category(category).build();
    }

    @Test
    @DisplayName("Create product")
    void createProduct() {
        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getId()).isNotNull();
    }

    @Test
    @DisplayName("Get all products")
    void getAllProducts() {
       Product product1 = Product.builder().name("Milk").description("Milk description")
                .quantity(1).price(new BigDecimal(100)).category(category).build();
        Product product2 = Product.builder().name("Beer").description("Beer description")
                .quantity(1).price(new BigDecimal(200)).category(category).build();
        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> products = productRepository.findAll();

        assertThat(products).hasSize(2).extracting(Product::getName)
                .containsExactlyInAnyOrder("Milk", "Beer");
    }

    @Test
    @DisplayName("Get product by id")
    void getCategoryById() {
        Product savedProduct = productRepository.save(product);
        Optional<Product> productFromDB = productRepository.findById(savedProduct.getId());

        assertThat(productFromDB).isPresent();
        assertThat(productFromDB.get().getName()).isEqualTo(savedProduct.getName());
    }

    @Test
    @DisplayName("Find products by category and order by price ascending")
    void findByCategoryOrderByPriceAsc() {
        Category savedCategory = categoryRepository.save(category);

        Product product1 = Product.builder().name("Milk").description("Milk desc")
                .quantity(1).price(new BigDecimal("3.00")).category(savedCategory).build();
        Product product2 = Product.builder().name("Juice").description("Juice desc")
                .quantity(1).price(new BigDecimal("1.50")).category(savedCategory).build();

        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(savedCategory);

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getPrice()).isEqualByComparingTo(new BigDecimal("1.50"));
        assertThat(products.get(1).getPrice()).isEqualByComparingTo(new BigDecimal("3.00"));
    }

    @Test
    @DisplayName("Find products by name like")
    void findByNameLikeIgnoreCase() {
        categoryRepository.save(category);

        Product product1 = Product.builder().name("Milk").description("desc")
                .quantity(1).price(new BigDecimal("3.00")).category(category).build();
        Product product2 = Product.builder().name("MILKshake").description("desc")
                .quantity(1).price(new BigDecimal("5.00")).category(category).build();
        Product product3 = Product.builder().name("Juice").description("desc")
                .quantity(1).price(new BigDecimal("2.00")).category(category).build();

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        List<Product> result = productRepository.findByNameLikeIgnoreCase("%milk%");

        assertThat(result).hasSize(2)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Milk", "MILKshake");
    }

    @Test
    @DisplayName("Update product")
    void updateProduct() {
        Product savedProduct = productRepository.save(product);
        savedProduct.setDescription("New description");
        Product updatedProduct = productRepository.save(savedProduct);

        assertThat(updatedProduct.getDescription()).isEqualTo(savedProduct.getDescription());
    }

    @Test
    @DisplayName("Delete product")
    void deleteCategory() {
        Product savedProduct = productRepository.save(product);
        productRepository.delete(savedProduct);
        Optional<Product> product = productRepository.findById(savedProduct.getId());

        assertThat(product).isNotPresent();
        assertThat(productRepository.existsById(savedProduct.getId())).isFalse();
    }
}