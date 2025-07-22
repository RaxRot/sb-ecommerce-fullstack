package com.raxrot.back.repoitories;

import com.raxrot.back.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
