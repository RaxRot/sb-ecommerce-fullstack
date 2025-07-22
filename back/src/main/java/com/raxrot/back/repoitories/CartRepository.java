package com.raxrot.back.repoitories;

import com.raxrot.back.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
