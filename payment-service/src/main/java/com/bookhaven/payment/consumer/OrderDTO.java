package com.bookhaven.payment.consumer;

import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderDTO implements Serializable {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private LocalDateTime createTime;
}
