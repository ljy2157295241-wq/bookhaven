package com.bookhaven.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String description;
    private String coverImage;
    private BigDecimal price;
    private Long categoryId;
    private Integer stock;
    private Integer sales;
    private String status; // ON_SHELF, OFF_SHELF

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
