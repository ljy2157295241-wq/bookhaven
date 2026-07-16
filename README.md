# BookHaven 在线图书商城

基于微服务架构的在线图书商城系统 — 《软件服务》课程项目实践。

## 技术栈

| 层级 | 技术 |
|---|---|
| 前端 | Vue 3 + TypeScript + Element Plus |
| 后端 | Java 17 + Spring Boot 3.4.x |
| 微服务生态 | Spring Cloud Alibaba 2023.0.x |
| 注册/配置中心 | Nacos 2.4.x |
| 网关 | Spring Cloud Gateway |
| 服务调用 | OpenFeign + Resilience4j |
| 分布式事务 | Seata AT 模式 |
| 消息队列 | RabbitMQ |
| 缓存 | Redis 7 |
| 数据库 | MySQL 8.4 |
| 部署 | Docker + Docker Compose |

## 微服务列表 (5个)

| 服务 | 端口 | 职责 |
|---|---|---|
| user-service | 8081 | 用户注册登录、JWT认证、地址管理 |
| product-service | 8082 | 图书CRUD、分类管理、库存管理 |
| cart-service | 8083 | 购物车管理 |
| order-service | 8084 | 订单创建、状态流转、分布式事务 |
| payment-service | 8085 | 支付单管理、MOCK支付 |
| gateway | 8080 | 统一入口、路由转发、CORS |

## 核心跨服务流程

用户下单 → Cart Service(获取购物车) → Product Service(扣库存) → Order Service(创建订单) → MQ通知 → Payment Service(创建支付单)

## 快速启动

```bash
# 1. 构建项目
mvn clean package -DskipTests

# 2. 启动基础设施 + 所有微服务
docker-compose up -d

# 3. 访问
#   Gateway: http://localhost:8080
#   Nacos:   http://localhost:8848/nacos
#   RabbitMQ: http://localhost:15672
```

## 项目结构

```
bookhaven/
├── pom.xml                          # 父POM (聚合工程)
├── bookhaven-common/                # 公共模块 (DTO、异常、统一响应)
├── bookhaven-gateway/               # API网关
├── user-service/                    # 用户服务
├── product-service/                 # 商品服务
├── order-service/                   # 订单服务 (核心)
├── cart-service/                    # 购物车服务
├── payment-service/                 # 支付服务
├── sql/                             # 数据库初始化脚本
├── docs/                            # 课程报告文档
└── docker-compose.yml               # Docker编排
```
