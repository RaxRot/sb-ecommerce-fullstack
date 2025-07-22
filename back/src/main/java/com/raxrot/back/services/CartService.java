package com.raxrot.back.services;

import com.raxrot.back.dtos.CartDTO;

public interface CartService {
    public CartDTO addProductToCart(Long productId, Integer quantity);
}
