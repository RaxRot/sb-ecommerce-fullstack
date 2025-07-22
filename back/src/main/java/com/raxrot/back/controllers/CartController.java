package com.raxrot.back.controllers;

import com.raxrot.back.dtos.CartDTO;
import com.raxrot.back.models.Cart;
import com.raxrot.back.repoitories.CartRepository;
import com.raxrot.back.services.CartService;
import com.raxrot.back.util.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;
    private final AuthUtil authUtil;
    private final CartRepository cartRepository;
    public CartController(CartService cartService, AuthUtil authUtil, CartRepository cartRepository) {
        this.cartService=cartService;
        this.authUtil=authUtil;
        this.cartRepository=cartRepository;
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO>addProductToCart(@PathVariable Long productId,@PathVariable Integer quantity){
        CartDTO cartDTO=cartService.addProductToCart(productId,quantity);
        return ResponseEntity.ok(cartDTO);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO>cartDTOS=cartService.getAllCarts();
        return ResponseEntity.ok(cartDTOS);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){
        String emailId=authUtil.loggedInEmail();
        Cart cart=cartRepository.findCartByEmail(emailId);
        Long cartId=cart.getId();
        CartDTO cartDTO=cartService.getCart(emailId,cartId);
        return ResponseEntity.ok(cartDTO);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,@PathVariable String operation){
        Integer operationToPath=operation.equalsIgnoreCase("delete")?-1:1;
       CartDTO cartDTO= cartService.updateProductQuantityInCart(productId,operationToPath);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFormCart(@PathVariable Long cartId,@PathVariable Long productId){
       String status = cartService.deleteProductFromCart(cartId,productId);
       return ResponseEntity.ok(status);
    }
}
