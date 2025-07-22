package com.raxrot.back.services;

import com.raxrot.back.dtos.CartDTO;

import java.util.List;

public interface CartService {
    public CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);

    CartDTO updateProductQuantityInCart(Long productId, Integer operationToPath);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long id, Long productId);
}
