# 第5章 系统实现

## 5.1 项目结构概览

```
bookhaven/
├── pom.xml                                  # 父POM (Maven多模块聚合)
├── bookhaven-common/                        # 公共模块
│   ├── model/CommonResult.java              # 统一响应体
│   ├── model/PageDTO.java                   # 分页对象
│   └── exception/                           # 异常处理
├── bookhaven-gateway/                       # API网关
│   ├── GatewayApplication.java
│   ├── config/CorsConfig.java               # 跨域配置
│   └── application.yml                      # 路由规则
├── user-service/                            # 用户服务
│   ├── UserServiceApplication.java
│   ├── config/JwtConfig.java                # JWT生成与验证
│   ├── entity/User.java
│   ├── service/UserService.java
│   └── controller/UserController.java
├── product-service/                         # 商品服务
│   ├── entity/Book.java, Category.java
│   ├── service/ProductService.java          # 库存操作
│   └── controller/ProductController.java
├── order-service/                           # 订单服务 (核心)
│   ├── feign/ProductFeignClient.java        # Feign调用Product
│   ├── feign/CartFeignClient.java           # Feign调用Cart
│   ├── config/RabbitMQConfig.java           # MQ配置
│   ├── config/Resilience4jConfig.java       # 熔断配置
│   ├── service/OrderService.java            # 分布式事务
│   └── consumer/PaymentResultConsumer.java  # 支付结果消费
├── cart-service/                            # 购物车服务
│   ├── feign/ProductFeignClient.java
│   ├── service/CartService.java
│   └── controller/CartController.java
├── payment-service/                         # 支付服务
│   ├── service/PaymentService.java          # MOCK支付
│   ├── consumer/PaymentCreateConsumer.java  # 创建支付单消费
│   └── controller/PaymentController.java
├── sql/                                     # 初始化脚本
│   ├── init-user.sql
│   ├── init-product.sql
│   ├── init-cart.sql
│   ├── init-order.sql
│   ├── init-payment.sql
│   └── init-seata.sql
└── docker-compose.yml                       # 全服务编排
```

## 5.2 核心功能实现: 跨服务下单

### 5.2.1 OrderService 分布式事务 (Seata AT)

```java
@GlobalTransactional(name = "create-order-tx", rollbackFor = Exception.class)
@Transactional(rollbackFor = Exception.class)
public Order createOrder(Long userId, List<Long> cartItemIds, ...) {
    // 1. 调用Cart Service获取购物车商品
    List<CartItemDTO> cartItems = cartFeignClient.getSelectedItems(userId, cartItemIds).getData();

    // 2. 创建订单
    Order order = new Order();
    order.setOrderNo(generateOrderNo());
    order.setStatus("PENDING_PAY");
    orderRepository.insert(order);

    // 3. 调用Product Service扣减库存 (跨服务 + 熔断保护)
    for (CartItemDTO item : cartItems) {
        Boolean deducted = circuitBreakerFactory.create("product-service")
            .run(() -> productFeignClient.deductStock(item.getBookId(), item.getQuantity()).getData(),
                  throwable -> { throw new BusinessException("Stock service unavailable"); });
    }

    // 4. 清空购物车 (跨服务)
    cartFeignClient.clearCheckedItems(userId, cartItemIds);

    // 5. MQ通知Payment Service创建支付单 (异步解耦)
    rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, PAYMENT_ROUTING_KEY, order);

    return order;
}
```

### 5.2.2 Resilience4j 熔断配置

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        sliding-window-size: 10          # 滑动窗口
        minimum-number-of-calls: 5       # 最少调用次数
        failure-rate-threshold: 50       # 50%失败触发熔断
        wait-duration-in-open-state: 10s # 熔断持续时间
        permitted-number-of-calls-in-half-open-state: 3
```

### 5.2.3 Gateway 路由配置

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
```

## 5.3 幂等性设计

1. **支付回调幂等**: 检查 `order.status !== "PENDING_PAY"` 时直接返回成功，防止重复更新
2. **创建支付单幂等**: 按 `orderNo` 查询已存在 PENDING 支付单时直接返回
3. **库存扣减幂等**: Seata AT 模式通过 undo_log 表确保回滚幂等

## 5.4 数据库表关系

```
user_service.user (1) ──── (N) user_service.user_address
                           (N) cart_service.cart_item ──── product_service.book
                           (N) order_service.order_info ──── order_item ──── product_service.book
                                                      └─── order_log
                                                      └─── payment_service.payment ──── refund
```

## 5.5 Docker Compose 编排

完整的 `docker-compose.yml` 编排了 8 个容器:

| 容器 | 镜像 | 端口 |
|---|---|---|
| mysql | mysql:8.4 | 3306 |
| redis | redis:7-alpine | 6379 |
| rabbitmq | rabbitmq:3.13-management | 5672, 15672 |
| nacos | nacos/nacos-server:v2.4.3 | 8848 |
| seata-server | seataio/seata-server:2.2.0 | 8091 |
| user-service | 本地构建 | 8081 |
| product-service | 本地构建 | 8082 |
| cart-service | 本地构建 | 8083 |
| order-service | 本地构建 | 8084 |
| payment-service | 本地构建 | 8085 |
| gateway | 本地构建 | 8080 |

## 5.6 启动步骤

```bash
# 1. 编译所有模块
cd bookhaven
mvn clean package -DskipTests

# 2. 启动全部服务
docker-compose up -d

# 3. 访问
#   - 前端:    http://localhost:8080
#   - Nacos:   http://localhost:8848 (账号: nacos / nacos)
#   - RabbitMQ: http://localhost:15672 (账号: guest / guest)
```

## 5.7 故障测试方案

| 测试场景 | 操作 | 预期结果 |
|---|---|---|
| Product Service 宕机 | `docker stop bookhaven-product` | Order下单触发熔断，返回"服务暂不可用" |
| Product Service 恢复 | `docker start bookhaven-product` | 熔断器半开→关闭，服务自动恢复 |
| 下单时库存不足 | 手动修改 stock=0 | Seata 回滚整个分布式事务 |
| 重复支付回调 | 调用两次 payment-success | 幂等校验，第二次直接返回成功 |
| MQ 消息丢失 | 停掉 RabbitMQ 再启动 | 持久化队列，重启后继续消费 |
