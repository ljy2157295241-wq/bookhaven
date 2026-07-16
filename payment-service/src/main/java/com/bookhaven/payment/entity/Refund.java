package com.bookhaven.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("refund")
public class Refund {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String refundNo;
    private Long paymentId;
    private BigDecimal amount;
    private String reason;
    private String status;   // APPLYING, SUCCESS, FAILED

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime refundTime;
}
