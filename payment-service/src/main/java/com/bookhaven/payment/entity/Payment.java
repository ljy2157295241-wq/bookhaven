package com.bookhaven.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String paymentNo;
    private String orderNo;
    private Long orderId;
    private BigDecimal amount;
    private String status;     // PENDING, SUCCESS, FAILED, REFUNDED
    private String payMethod;  // ALIPAY, WECHAT, MOCK

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime payTime;
}
