package com.raxrot.back.controllers;

import com.raxrot.back.configurations.AppConfig;
import com.raxrot.back.dtos.ProductDTO;
import com.raxrot.back.dtos.ProductResponse;
import com.raxrot.back.services.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@PathVariable Long categoryId, @RequestBody @Valid ProductDTO productDto) {
        log.info("POST /admin/categories/{}/product - Request: {}", categoryId, productDto);
        ProductDTO createdProduct = productService.addProduct(categoryId, productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(value = "page", defaultValue = AppConfig.PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", defaultValue = AppConfig.PAGE_SIZE) Integer size,
            @RequestParam(value = "sortBy", defaultValue = AppConfig.SORT_BY_ID) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppConfig.SORT_ORDER_ASC) String sortOrder
    ) {
        log.info("GET /public/products?page={}&size={}&sortBy={}&sortOrder={}", page, size, sortBy, sortOrder);
        ProductResponse productResponse = productService.getAllProducts(page, size, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        log.info("GET /public/categories/{}/products", categoryId);
        ProductResponse productResponse = productService.searchByCategory(categoryId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword) {
        log.info("GET /public/products/keyword/{}", keyword);
        ProductResponse productResponse = productService.searchProductsByKeyWord(keyword);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody @Valid ProductDTO productDto) {
        log.info("PUT /admin/products/{} - Request: {}", productId, productDto);
        ProductDTO updatedProduct = productService.updateProduct(productId, productDto);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        log.info("DELETE /admin/products/{}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO>updateProductImage(@PathVariable Long productId,@RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedProduct = productService.uploadProductImage(productId,image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
