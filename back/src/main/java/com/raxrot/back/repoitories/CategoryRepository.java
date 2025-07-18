package com.raxrot.back.repoitories;

import com.raxrot.back.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
