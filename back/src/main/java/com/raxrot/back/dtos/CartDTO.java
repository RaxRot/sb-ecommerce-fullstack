package com.raxrot.back.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private BigDecimal totalPrice=BigDecimal.ZERO;
    private List<ProductDTO>products=new ArrayList<>();
}
