package com.raxrot.back.services.Impl;

import com.raxrot.back.dtos.ProductDTO;
import com.raxrot.back.dtos.ProductResponse;
import com.raxrot.back.models.Category;
import com.raxrot.back.models.Product;
import com.raxrot.back.repoitories.CategoryRepository;
import com.raxrot.back.repoitories.ProductRepository;
import com.raxrot.back.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private FileService fileService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Electronics").build();
        product = Product.builder().id(1L).name("Laptop").price(new BigDecimal(1200.0)).quantity(10).description("Powerful laptop").image("default.png").category(category).build();
        productDTO = ProductDTO.builder().id(1L).name("Laptop").price(new BigDecimal(1200.0)).quantity(10).description("Powerful laptop").image("default.png").build();
    }

    @Test
    @DisplayName("Add product")
    void addProduct() {
        // given
        BDDMockito.given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        BDDMockito.given(modelMapper.map(productDTO, Product.class)).willReturn(product);
        BDDMockito.given(productRepository.save(any(Product.class))).willReturn(product);
        BDDMockito.given(modelMapper.map(product, ProductDTO.class)).willReturn(productDTO);

        // when
        ProductDTO result = productService.addProduct(1L, productDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
        BDDMockito.verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Get all products paginated")
    void getAllProducts() {
        // given
        List<Product> productList = List.of(product);
        Page<Product> productPage = new PageImpl<>(productList);
        BDDMockito.given(productRepository.findAll(any(Pageable.class))).willReturn(productPage);
        BDDMockito.given(modelMapper.map(product, ProductDTO.class)).willReturn(productDTO);

        // when
        ProductResponse response = productService.getAllProducts(0, 10, "name", "asc");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Search by category")
    void searchByCategory() {
        // given
        List<Product> productList = List.of(product);
        BDDMockito.given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        BDDMockito.given(productRepository.findByCategoryOrderByPriceAsc(category)).willReturn(productList);
        BDDMockito.given(modelMapper.map(product, ProductDTO.class)).willReturn(productDTO);

        // when
        ProductResponse response = productService.searchByCategory(1L);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Search by keyword")
    void searchProductsByKeyword() {
        // given
        List<Product> productList = List.of(product);
        BDDMockito.given(productRepository.findByNameLikeIgnoreCase("%Laptop%")).willReturn(productList);
        BDDMockito.given(modelMapper.map(product, ProductDTO.class)).willReturn(productDTO);

        // when
        ProductResponse response = productService.searchProductsByKeyWord("Laptop");

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Update product")
    void updateProduct() {
        // given
        ProductDTO updatedDTO = ProductDTO.builder().name("Gaming Laptop").description("Updated").price(new BigDecimal(1200.0)).quantity(5).build();
        product.setName("Gaming Laptop");
        product.setDescription("Updated");
        product.setPrice(new BigDecimal(1200.0));
        product.setQuantity(5);

        BDDMockito.given(productRepository.findById(1L)).willReturn(Optional.of(product));
        BDDMockito.given(productRepository.save(product)).willReturn(product);
        BDDMockito.given(modelMapper.map(product, ProductDTO.class)).willReturn(updatedDTO);

        // when
        ProductDTO result = productService.updateProduct(1L, updatedDTO);

        // then
        assertThat(result.getName()).isEqualTo("Gaming Laptop");
        assertThat(result.getDescription()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("Delete product")
    void deleteProduct() {
        // given
        BDDMockito.given(productRepository.findById(1L)).willReturn(Optional.of(product));
        BDDMockito.willDoNothing().given(productRepository).delete(product);

        // when
        productService.deleteProduct(1L);

        // then
        BDDMockito.verify(productRepository).delete(product);
    }
}
