-- bookhaven_product database
CREATE DATABASE IF NOT EXISTS bookhaven_product DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookhaven_product;

CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

CREATE TABLE IF NOT EXISTS `book` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `isbn` VARCHAR(20) DEFAULT NULL COMMENT 'ISBN编号',
    `title` VARCHAR(200) NOT NULL COMMENT '书名',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
    `publisher` VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `status` VARCHAR(20) DEFAULT 'ON_SHELF' COMMENT '状态: ON_SHELF, OFF_SHELF',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_title` (`title`),
    INDEX `idx_category` (`category_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 初始数据
INSERT INTO `category` (`id`, `name`, `parent_id`, `sort`) VALUES
(1, '文学', 0, 1),
(2, '科幻', 0, 2),
(3, '计算机', 0, 3),
(4, '历史', 0, 4),
(5, '哲学', 0, 5);

INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `price`, `category_id`, `stock`, `status`) VALUES
('9787544291163', '百年孤独', '加西亚·马尔克斯', '南海出版公司', 55.00, 1, 100, 'ON_SHELF'),
('9787544380416', '三体', '刘慈欣', '重庆出版社', 68.00, 2, 200, 'ON_SHELF'),
('9787115428028', '深入理解Java虚拟机', '周志明', '机械工业出版社', 129.00, 3, 150, 'ON_SHELF'),
('9787508684031', '人类简史', '尤瓦尔·赫拉利', '中信出版社', 68.00, 4, 80, 'ON_SHELF'),
('9787108005821', '论语译注', '杨伯峻', '中华书局', 29.00, 5, 120, 'ON_SHELF');
