package com.raxrot.back.services.Impl;

import com.raxrot.back.configurations.AppConfig;
import com.raxrot.back.dtos.CartDTO;
import com.raxrot.back.dtos.ProductDTO;
import com.raxrot.back.dtos.ProductResponse;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.Cart;
import com.raxrot.back.models.Category;
import com.raxrot.back.models.Product;
import com.raxrot.back.repoitories.CartRepository;
import com.raxrot.back.repoitories.CategoryRepository;
import com.raxrot.back.repoitories.ProductRepository;
import com.raxrot.back.services.CartService;
import com.raxrot.back.services.FileService;
import com.raxrot.back.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final CartRepository cartRepository;
    private final CartService cartService;

    @Value("${project.image}")
    private String path;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ModelMapper modelMapper,
                              FileService fileService,
                              CartRepository cartRepository,
                              CartService cartService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
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
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        log.info("Fetching products - page: {}, size: {}, sortBy: {}, sortOrder: {}",
                pageNumber, pageSize, sortBy, sortOrder);

        Page<Product> productPage = productRepository.findAll(pageable);
        List<Product> products = productPage.getContent();

        log.info("Found {} product(s) on current page", products.size());

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

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

        List<Cart>carts=cartRepository.findCartsByProductId(productId);
        List<CartDTO>cartDTOS=carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO>productDTOS=cart.getCartItems().stream()
                    .map(p->modelMapper.map(p.getProduct(), ProductDTO.class))
                    .toList();
            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();

        cartDTOS.forEach(cart -> cartService.updateProductInCarts(cart.getId(),productId));


        log.info("Product with ID {} successfully updated", productId);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public void deleteProduct(Long productId) {
        log.warn("Deleting product with ID: {}", productId);
        Product product = getProduct(productId);

        List<Cart>carts=cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getId(),productId));

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
        log.info("Uploading image for product ID: {}", productId);

        // Get product
        Product product = getProduct(productId);
        log.debug("Product found: id={}, name={}", product.getId(), product.getName());

        // Upload image
        String fileName = fileService.uploadImage(path, image);
        log.info("Image uploaded: {}", fileName);

        // Update image field
        product.setImage(fileName);
        Product updatedProduct = productRepository.save(product);
        log.info("Product image updated in DB: id={}, image={}", updatedProduct.getId(), updatedProduct.getImage());

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

}
