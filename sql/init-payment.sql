-- bookhaven_payment database
CREATE DATABASE IF NOT EXISTS bookhaven_payment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookhaven_payment;

CREATE TABLE IF NOT EXISTS `payment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `payment_no` VARCHAR(32) NOT NULL COMMENT '支付单号',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING, SUCCESS, FAILED, REFUNDED',
    `pay_method` VARCHAR(20) DEFAULT 'MOCK' COMMENT '支付方式: ALIPAY, WECHAT, MOCK',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';

CREATE TABLE IF NOT EXISTS `refund` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `refund_no` VARCHAR(32) NOT NULL COMMENT '退款单号',
    `payment_id` BIGINT NOT NULL COMMENT '支付ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '退款原因',
    `status` VARCHAR(20) DEFAULT 'APPLYING' COMMENT '状态: APPLYING, SUCCESS, FAILED',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `refund_time` DATETIME DEFAULT NULL COMMENT '退款时间',
    UNIQUE KEY `uk_refund_no` (`refund_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款表';
