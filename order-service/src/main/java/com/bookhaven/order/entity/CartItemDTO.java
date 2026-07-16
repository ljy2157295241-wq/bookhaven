package com.bookhaven.order.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private String bookCover;
    private BigDecimal bookPrice;
    private Integer quantity;
}
