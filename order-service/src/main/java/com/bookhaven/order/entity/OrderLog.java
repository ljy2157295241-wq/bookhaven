package com.bookhaven.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("order_log")
public class OrderLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String fromStatus;
    private String toStatus;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
