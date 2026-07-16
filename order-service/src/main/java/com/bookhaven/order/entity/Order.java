package com.bookhaven.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@TableName("order_info")
public class Order implements java.io.Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;       // 璁㈠崟缂栧彿
    private Long userId;
    private BigDecimal totalAmount;
    private String status;        // PENDING_PAY, PAID, SHIPPED, DELIVERED, CANCELLED, REFUNDING
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
}
