package com.raxrot.back.repoitories;

import com.raxrot.back.models.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("select ci from CartItem ci where ci.cart.id=?1 and ci.product.id=?2")
    CartItem findCartItemByProductIdAndCartId(Long id, Long productId);

    @Transactional
    @Modifying
    @Query("delete from CartItem ci where ci.cart.id=?1 and ci.product.id=?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}
