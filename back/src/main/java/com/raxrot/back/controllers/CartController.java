package com.raxrot.back.controllers;

import com.raxrot.back.dtos.CartDTO;
import com.raxrot.back.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService){
        this.cartService=cartService;
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO>addProductToCart(@PathVariable Long productId,@PathVariable Integer quantity){
        CartDTO cartDTO=cartService.addProductToCart(productId,quantity);
        return ResponseEntity.ok(cartDTO);
    }
}
