package com.raxrot.back.services;

import com.raxrot.back.dtos.ProductDTO;
import com.raxrot.back.dtos.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, @Valid ProductDTO productDto);
    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductResponse searchByCategory(Long categoryId);
    ProductResponse searchProductsByKeyWord(String keyword);
    ProductDTO updateProduct(Long productId, @Valid ProductDTO productDto);
    void deleteProduct(Long productId);

    ProductDTO uploadProductImage(Long productId, MultipartFile image) throws IOException;
}
