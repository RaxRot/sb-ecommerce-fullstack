package com.raxrot.back.repoitories;

import com.raxrot.back.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
