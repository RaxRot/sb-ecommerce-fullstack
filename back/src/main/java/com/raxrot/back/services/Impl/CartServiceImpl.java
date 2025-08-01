package com.raxrot.back.services.Impl;

import com.raxrot.back.dtos.CartDTO;
import com.raxrot.back.dtos.ProductDTO;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.Cart;
import com.raxrot.back.models.CartItem;
import com.raxrot.back.models.Product;
import com.raxrot.back.repoitories.CartItemRepository;
import com.raxrot.back.repoitories.CartRepository;
import com.raxrot.back.repoitories.ProductRepository;
import com.raxrot.back.services.CartService;
import com.raxrot.back.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final AuthUtil authUtil;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;
    public CartServiceImpl(CartRepository cartRepository,
                           AuthUtil authUtil,
                           ProductRepository productRepository,
                           CartItemRepository cartItemRepository,
                           ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.authUtil = authUtil;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
       Cart cart=createCart();

        Product product=productRepository.findById(productId).orElseThrow(()->new ApiException("Product not found"));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getId(),productId);
        if(cartItem!=null) {
            throw new ApiException("Product already in use");
        }
        if (product.getQuantity()==0){
            throw new ApiException("Product is not available");
        }
        if (product.getQuantity()<quantity){
            throw new ApiException("Please make an order less than or equal to "+product.getQuantity());
        }

        CartItem newCartItem=new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setProductPrice(product.getPrice());
         cartItemRepository.save(newCartItem);
         //will reduce quantity of stock in payment
         product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice().add(product.getPrice().multiply(BigDecimal.valueOf(quantity))));
        cartRepository.save(cart);

        CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);

        List<CartItem>cartItems=cart.getCartItems();
        Stream<ProductDTO>productDTOStream=cartItems.stream().map(item->{
            ProductDTO productDTO=modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });
        cartDTO.setProducts(productDTOStream.collect(Collectors.toList()));
        return cartDTO;
    }

    private Cart createCart(){
        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(new BigDecimal(0.0));
        cart.setUser(authUtil.loggedInUser());
        Cart newCart=cartRepository.save(cart);
        return newCart;
    }

    @Override
    public List<CartDTO> getAllCarts() {
       List<Cart>carts=cartRepository.findAll();
       if(carts.size()==0) {
           throw new ApiException("No cart exists");
       }
       List<CartDTO>cartDTOS=carts.stream().map(cart -> {
           CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);
           List<ProductDTO>productDTOS=cart.getCartItems().stream()
                   .map(product->modelMapper.map(product.getProduct(), ProductDTO.class))
                   .collect(Collectors.toList());
           cartDTO.setProducts(productDTOS);
           return cartDTO;
       }).collect(Collectors.toList());
       return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart=cartRepository.findCartByEmailAndCartId(emailId,cartId);
        if (cart == null) {
            throw new ApiException("Cart not found");
        }
        CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(cartItem -> {cartItem.getProduct().setQuantity(cartItem.getQuantity());});
        List<ProductDTO>productDTOS=cart.getCartItems().stream().map(product->modelMapper.map(product.getProduct(), ProductDTO.class)).collect(Collectors.toList());
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer operationToPath) {
        String emailId=authUtil.loggedInEmail();
        Cart userCart=cartRepository.findCartByEmail(emailId);
        Long cartId=userCart.getId();

        Cart cart=cartRepository.findById(cartId).orElseThrow(()->new ApiException("Cart not found"));

        Product product=productRepository.findById(productId).orElseThrow(()->new ApiException("Product not found"));

        if (product.getQuantity()==0){
            throw new ApiException("Product is not available");
        }
        if (product.getQuantity()<operationToPath){
            throw new ApiException("Please make an order less than or equal to "+product.getQuantity());
        }

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if (cartItem == null) {
            throw new ApiException("Cart item not found");
        }

        int newQuantity=cartItem.getQuantity()+operationToPath;
        if (newQuantity<0){
            throw new ApiException("The result can't be negative");
        }
        if (newQuantity==0){
            deleteProductFromCart(cartId,productId);
            return modelMapper.map(cart, CartDTO.class);
        }

        cartItem.setProductPrice(product.getPrice());
        cartItem.setQuantity(cartItem.getQuantity()+operationToPath);
        cart.setTotalPrice(cart.getTotalPrice().add(cartItem.getProductPrice().multiply(BigDecimal.valueOf(operationToPath))));
        cartRepository.save(cart);
        CartItem updatedCartItem=cartItemRepository.save(cartItem);
        if(updatedCartItem.getQuantity()==0) {
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());
        }
        CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);
        List<CartItem>cartItems=cart.getCartItems();
        Stream<ProductDTO>productDTOStream=cartItems.stream().map(item->{
            ProductDTO productDTO=modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });
        cartDTO.setProducts(productDTOStream.collect(Collectors.toList()));
        return cartDTO;
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart=cartRepository.findById(cartId).orElseThrow(()->new ApiException("Cart not found"));
        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if (cartItem == null) {
            throw new ApiException("Cart item not found");
        }
        cart.setTotalPrice(cart.getTotalPrice().subtract(cartItem.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

        return "Product deleted "+ cartItem.getProduct().getName();
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ApiException("Cart not found"));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new ApiException("Product not available in cart");
        }

        BigDecimal newProductPrice = cartItem.getProduct().getPrice();

        BigDecimal oldTotal = cart.getTotalPrice()
                .subtract(cartItem.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

        cartItem.setProductPrice(newProductPrice);

        BigDecimal updatedTotal = oldTotal.add(newProductPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cart.setTotalPrice(updatedTotal);

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }
}
