# 第3章 微服务设计

## 3.1 微服务拆分原则

采用 **领域驱动设计 (DDD)** 的限界上下文思想进行服务拆分：

1. **单一职责**: 每个微服务只负责一个明确的业务领域
2. **独立数据**: 每个微服务拥有独立的数据库 Schema
3. **自治部署**: 每个微服务可独立构建、部署、扩缩容
4. **轻量通信**: 服务间通过 RESTful API（同步）和 RabbitMQ（异步）通信

## 3.2 服务拆分详情

### 3.2.1 User Service (用户服务)

- **业务范围**: 用户注册、登录、JWT Token 签发、收货地址管理
- **依赖关系**: 无其它服务依赖（所有服务依赖它的 JWT 鉴权机制）
- **数据存储**: `bookhaven_user` 数据库，含 `user`、`user_address` 表
- **对外接口**: 注册 / 登录 / 用户信息查询

### 3.2.2 Product Service (商品服务)

- **业务范围**: 图书 CRUD、分类管理、库存管理与扣减
- **依赖关系**: 被 Cart Service 和 Order Service 依赖（库存查询/扣减）
- **数据存储**: `bookhaven_product` 数据库，含 `book`、`category` 表
- **对外接口**: 图书搜索/详情/库存操作

### 3.2.3 Cart Service (购物车服务)

- **业务范围**: 购物车商品增删改查、选中结算
- **依赖关系**: 依赖 Product Service 获取图书价格快照
- **数据存储**: `bookhaven_cart` 数据库，含 `cart_item` 表
- **对外接口**: 购物车 CRUD、选中查询、清空

### 3.2.4 Order Service (订单服务) — 核心服务

- **业务范围**: 订单创建（跨服务分布式事务）、订单状态流转、超时取消
- **依赖关系**: 依赖 Product Service（扣库存/恢复库存）、Cart Service（获取购物车/清空已购）
- **依赖**: Payment Service 的 MQ 回调
- **数据存储**: `bookhaven_order` 数据库，含 `order_info`、`order_item`、`order_log` 表
- **对外接口**: 创建订单、查询订单、取消订单、支付回调

### 3.2.5 Payment Service (支付服务)

- **业务范围**: 支付单创建、模拟支付、退款
- **依赖关系**: 被 Order Service 通过 MQ 异步触发；支付后回调 Order Service
- **数据存储**: `bookhaven_payment` 数据库，含 `payment`、`refund` 表
- **对外接口**: 创建支付单、模拟支付、退款

## 3.3 服务间调用关系图

```
                    ┌─────────────┐
                    │ Cart Service│
                    └──────┬──────┘
                           │ getSelectedItems()
                           │ clearCheckedItems()
                    ┌──────▼──────┐
    ┌─────────┐     │ Order Service│     ┌────────────┐
    │ Product │◄────┤  (核心)     │─────►│ Payment    │
    │ Service │     │             │ MQ   │ Service    │
    └─────────┘     └─────────────┘      └────────────┘
     deductStock()          │                  │
     restoreStock()         │ MQ callback      │ MQ payment.create
                            └──────────────────┘
```

## 3.4 分布式数据一致性策略

| 场景 | 策略 | 实现方式 |
|---|---|---|
| 创建订单（扣库存+创建订单+清空购物车） | Seata AT 分布式事务 | @GlobalTransactional |
| 支付通知订单（支付成功→更新订单） | 最终一致性 + MQ 可靠投递 | RabbitMQ + 幂等处理 |
| 取消订单（恢复库存+更新订单） | Seata AT 分布式事务 | @GlobalTransactional |
| 支付回调（防止重复更新） | 幂等性校验 | 检查订单状态是否已变更 |
| 下单超时取消 | 延迟消息 / 定时任务 | Scheduled Task 扫描超时订单 |
