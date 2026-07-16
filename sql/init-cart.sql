-- bookhaven_cart database
CREATE DATABASE IF NOT EXISTS bookhaven_cart DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookhaven_cart;

CREATE TABLE IF NOT EXISTS `cart_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `book_title` VARCHAR(200) NOT NULL COMMENT '书名(快照)',
    `book_cover` VARCHAR(255) DEFAULT NULL COMMENT '封面(快照)',
    `book_price` DECIMAL(10,2) NOT NULL COMMENT '单价(快照)',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    UNIQUE KEY `uk_user_book` (`user_id`, `book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
