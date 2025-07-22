package com.raxrot.back.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private CartDTO cart;
    private ProductDTO product;
    private Integer quantity;
    private BigDecimal productPrice;
}
