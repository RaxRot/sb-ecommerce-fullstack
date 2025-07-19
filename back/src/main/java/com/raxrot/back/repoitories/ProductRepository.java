package com.raxrot.back.repoitories;

import com.raxrot.back.models.Category;
import com.raxrot.back.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product>findByCategoryOrderByPriceAsc(Category category);
    List<Product> findByNameLikeIgnoreCase(String keyword);
}
