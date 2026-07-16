# 第4章 微服务API、数据结构及说明

## 4.1 User Service API

| 方法 | 路径 | 说明 | 输入 | 输出 |
|---|---|---|---|---|
| POST | `/api/users/register` | 用户注册 | username, password, email | { token, userId, username } |
| POST | `/api/users/login` | 用户登录 | username, password | { token, userId, username } |
| GET | `/api/users/{id}` | 获取用户信息 | 无 | User 对象 |
| GET | `/api/users/info` | 获取当前用户 | Header: Authorization | User 对象 |

### 用户表 (user)

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT (PK) | 主键 |
| username | VARCHAR(50) UNIQUE | 用户名 |
| password | VARCHAR(128) | 密码 (SHA-256) |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| nickname | VARCHAR(50) | 昵称 |
| avatar | VARCHAR(255) | 头像 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

## 4.2 Product Service API

| 方法 | 路径 | 说明 | 输入 | 输出 |
|---|---|---|---|---|
| GET | `/api/products` | 搜索图书 | keyword, categoryId, page, size | PageDTO<Book> |
| GET | `/api/products/{id}` | 图书详情 | 无 | Book |
| POST | `/api/products` | 添加图书 (Admin) | Book JSON | Book |
| PUT | `/api/products/{id}` | 更新图书 (Admin) | Book JSON | Book |
| POST | `/api/products/{id}/deduct` | 扣减库存 (内部) | quantity | Boolean |
| POST | `/api/products/{id}/restore` | 恢复库存 (内部) | quantity | Boolean |
| GET | `/api/categories` | 分类列表 | 无 | List<Category> |

### 图书表 (book)

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT (PK) | 主键 |
| isbn | VARCHAR(20) | ISBN 编号 |
| title | VARCHAR(200) | 书名 |
| author | VARCHAR(100) | 作者 |
| publisher | VARCHAR(100) | 出版社 |
| price | DECIMAL(10,2) | 价格 |
| category_id | BIGINT | 分类 ID |
| stock | INT | 库存 |
| sales | INT | 销量 |
| status | VARCHAR(20) | ON_SHELL / OFF_SHELF |

## 4.3 Cart Service API

| 方法 | 路径 | 说明 | 输入 | 输出 |
|---|---|---|---|---|
| GET | `/api/cart` | 获取购物车 | Header: userId | List<CartItem> |
| POST | `/api/cart/items` | 添加商品 | CartItem JSON | CartItem |
| PUT | `/api/cart/items/{id}` | 修改数量 | quantity | 无 |
| DELETE | `/api/cart/items/{id}` | 删除项 | 无 | 无 |
| DELETE | `/api/cart/clear` | 清空购物车 | 无 | 无 |
| POST | `/api/cart/selected` | 获取选中项 (内部) | List<Long> ids | List<CartItem> |
| POST | `/api/cart/clear-checked` | 清空已购 (内部) | List<Long> ids | 无 |

## 4.4 Order Service API（核心）

| 方法 | 路径 | 说明 | 输入 | 输出 |
|---|---|---|---|---|
| POST | `/api/orders` | 创建订单 | cartItemIds, 收货信息 | Order |
| GET | `/api/orders/{id}` | 订单详情 | 无 | Order |
| GET | `/api/orders` | 用户订单列表 | Header: userId | List<Order> |
| GET | `/api/orders/{id}/items` | 订单商品列表 | 无 | List<OrderItem> |
| POST | `/api/orders/{id}/cancel` | 取消订单 | 无 | Boolean |
| POST | `/api/orders/payment-success` | 支付回调 (内部) | orderNo, paymentNo | Boolean |

### 订单表 (order_info)

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT (PK) | 主键 |
| order_no | VARCHAR(32) UNIQUE | 订单编号 (BH + 时间戳) |
| user_id | BIGINT | 用户 ID |
| total_amount | DECIMAL(10,2) | 总金额 |
| status | VARCHAR(20) | PENDING_PAY → PAID → SHIPPED → DELIVERED / CANCELLED |
| receiver_name/phone/address | VARCHAR | 收货信息 |

### 订单状态流转

```
PENDING_PAY ──► PAID ──► SHIPPED ──► DELIVERED
      │
      └──► CANCELLED  (仅待支付时可取消)
      └──► REFUNDING  (支付后可退款)
```

## 4.5 Payment Service API

| 方法 | 路径 | 说明 | 输入 | 输出 |
|---|---|---|---|---|
| POST | `/api/payments` | 创建支付单 | orderNo, orderId, amount | Payment |
| POST | `/api/payments/{paymentNo}/pay` | 模拟支付 | 无 | Payment |
| POST | `/api/payments/{paymentNo}/refund` | 退款 | 无 | Payment |

### 支付表 (payment)

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT (PK) | 主键 |
| payment_no | VARCHAR(32) UNIQUE | 支付单号 |
| order_no | VARCHAR(32) UNIQUE | 关联订单号 |
| amount | DECIMAL(10,2) | 金额 |
| status | VARCHAR(20) | PENDING → SUCCESS / FAILED / REFUNDED |
| pay_method | VARCHAR(20) | MOCK (模拟支付) |
