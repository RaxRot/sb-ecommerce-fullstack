package com.raxrot.back.services.Impl;

import com.raxrot.back.configurations.AppConfig;
import com.raxrot.back.dtos.ProductDTO;
import com.raxrot.back.dtos.ProductResponse;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.Category;
import com.raxrot.back.models.Product;
import com.raxrot.back.repoitories.CategoryRepository;
import com.raxrot.back.repoitories.ProductRepository;
import com.raxrot.back.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDto) {
        log.info("Adding product: {}", productDto.getName());
        Category category = getCategory(categoryId);

        Product product = modelMapper.map(productDto, Product.class);
        product.setImage("default.png");
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        log.info("Product '{}' added with ID {}", savedProduct.getName(), savedProduct.getId());
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        log.info("Found {} products", products.size());

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        log.info("Searching products by category ID: {}", categoryId);
        Category category = getCategory(categoryId);
        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        log.info("Found {} products for category '{}'", products.size(), category.getName());

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchProductsByKeyWord(String keyword) {
        log.info("Searching products by keyword: {}", keyword);
        List<Product> products = productRepository.findByNameLikeIgnoreCase('%' + keyword + '%');
        log.info("Found {} products matching keyword '{}'", products.size(), keyword);

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDto) {
        log.info("Updating product with ID: {}", productId);
        Product product = getProduct(productId);

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setQuantity(productDto.getQuantity());
        product.setPrice(productDto.getPrice());

        Product savedProduct = productRepository.save(product);
        log.info("Product with ID {} successfully updated", productId);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public void deleteProduct(Long productId) {
        log.warn("Deleting product with ID: {}", productId);
        Product product = getProduct(productId);
        productRepository.delete(product);
        log.warn("Product with ID {} deleted", productId);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found: ID {}", productId);
                    return new ApiException(AppConfig.NO_PRODUCT_FOUND);
                });
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found: ID {}", categoryId);
                    return new ApiException(AppConfig.NO_CATEGORY_FOUND);
                });
    }

    @Override
    public ProductDTO uploadProductImage(Long productId, MultipartFile image) throws IOException {
        //Get Product from DB
        Product product = getProduct(productId);

        //way where we will save image
        String path="images";
        //load image and make unique name
        String fileName=uploadImage(path,image);
        //upd image in product
        product.setImage(fileName);
        //save in DB
        Product updatedProduct = productRepository.save(product);
        //get back product
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    private String uploadImage(String path, MultipartFile image) throws IOException {
        //get final name
        String originalFilename = image.getOriginalFilename();
        //generate random ID
        String randomId= UUID.randomUUID().toString();
        //create unique name with extension
        String fileName=randomId.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));
        //full way to file
        String filePath=path+ File.separator+fileName;
        //check if images exists if no-create
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //copy content in that folder
        Files.copy(image.getInputStream(), Paths.get(filePath));
        //back unique name
        return fileName;
    }
}
