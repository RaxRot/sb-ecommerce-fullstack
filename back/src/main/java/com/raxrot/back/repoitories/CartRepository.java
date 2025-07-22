package com.raxrot.back.repoitories;

import com.raxrot.back.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select c from Cart c where c.user.email=?1")
    Cart findCartByEmail(String email);

    @Query("select c from Cart c where c.user.email=?1 and c.id=?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);

    @Query("select c from Cart c join fetch c.cartItems ci join fetch ci.product p where p.id=?1")
    List<Cart> findCartsByProductId(Long productId);
}
