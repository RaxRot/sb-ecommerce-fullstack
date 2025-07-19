package com.raxrot.back.controllers;

import com.raxrot.back.configurations.AppConfig;
import com.raxrot.back.dtos.ProductDTO;
import com.raxrot.back.dtos.ProductResponse;
import com.raxrot.back.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product API", description = "CRUD operations for products")
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Add product to category", description = "Adds a new product to a specific category")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@PathVariable Long categoryId, @RequestBody @Valid ProductDTO productDto) {
        log.info("POST /admin/categories/{}/product - Request: {}", categoryId, productDto);
        ProductDTO createdProduct = productService.addProduct(categoryId, productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "Get all products", description = "Retrieves paginated list of all products")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
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

    @Operation(summary = "Get products by category", description = "Fetches all products belonging to a specific category")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        log.info("GET /public/categories/{}/products", categoryId);
        ProductResponse productResponse = productService.searchByCategory(categoryId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @Operation(summary = "Search products by keyword", description = "Searches products by a given keyword")
    @ApiResponse(responseCode = "200", description = "Products found")
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword) {
        log.info("GET /public/products/keyword/{}", keyword);
        ProductResponse productResponse = productService.searchProductsByKeyWord(keyword);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @Operation(summary = "Update product", description = "Updates an existing product by its ID")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody @Valid ProductDTO productDto) {
        log.info("PUT /admin/products/{} - Request: {}", productId, productDto);
        ProductDTO updatedProduct = productService.updateProduct(productId, productDto);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        log.info("DELETE /admin/products/{}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload product image", description = "Uploads or updates the image for a product")
    @ApiResponse(responseCode = "200", description = "Image uploaded successfully")
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedProduct = productService.uploadProductImage(productId, image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
