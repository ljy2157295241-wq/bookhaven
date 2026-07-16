-- bookhaven_order database
CREATE DATABASE IF NOT EXISTS bookhaven_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookhaven_order;

CREATE TABLE IF NOT EXISTS `order_info` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAY' COMMENT '状态: PENDING_PAY, PAID, SHIPPED, DELIVERED, CANCELLED, REFUNDING',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `receiver_address` VARCHAR(200) NOT NULL COMMENT '收货地址',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
    UNIQUE KEY `uk_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `book_title` VARCHAR(200) NOT NULL COMMENT '书名(快照)',
    `book_cover` VARCHAR(255) DEFAULT NULL COMMENT '封面(快照)',
    `book_price` DECIMAL(10,2) NOT NULL COMMENT '单价(快照)',
    `quantity` INT NOT NULL COMMENT '数量',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

CREATE TABLE IF NOT EXISTS `order_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `from_status` VARCHAR(20) DEFAULT NULL COMMENT '原状态',
    `to_status` VARCHAR(20) NOT NULL COMMENT '目标状态',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单日志表';

-- Seata AT mode undo_log table (required by distributed transaction)
CREATE TABLE IF NOT EXISTS `undo_log` (
    `branch_id` BIGINT NOT NULL,
    `xid` VARCHAR(128) NOT NULL,
    `context` VARCHAR(128),
    `rollback_info` LONGBLOB,
    `log_status` INT,
    `log_created` DATETIME,
    `log_modified` DATETIME,
    `ext` VARCHAR(100),
    PRIMARY KEY (`branch_id`),
    KEY `idx_xid` (`xid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
