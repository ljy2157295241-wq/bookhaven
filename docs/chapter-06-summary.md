# 第6章 技术总结

## 6.1 开发过程总结

本次课程实践设计并实现了一个基于微服务架构的在线图书商城系统 "BookHaven"。项目从需求分析开始，经历了架构设计、服务拆分、数据库设计、编码实现、Docker 部署等完整流程。

### 完成情况

| 模块 | 完成内容 | 状态 |
|---|---|---|
| 系统设计 | 6 章完整报告文档 | ✅ |
| Common 公共模块 | 统一响应体、异常处理、分页对象 | ✅ |
| API 网关 | 路由转发、CORS 跨域 | ✅ |
| User Service | 注册登录、JWT 签发、地址管理 | ✅ |
| Product Service | 图书 CRUD、分类、库存管理和扣减 | ✅ |
| Cart Service | 购物车 CRUD、Feign 调用 Product | ✅ |
| Order Service | 创建订单、Seata 分布式事务、MQ 消息、熔断降级、幂等性 | ✅ |
| Payment Service | 支付单管理、MOCK 支付、MQ 回调 | ✅ |
| 数据库 | 6 个库、12 张表、初始数据 | ✅ |
| 部署 | Docker Compose 编排全部服务 | ✅ |

## 6.2 个人学习情况

### 6.2.1 微服务架构设计

- 学习了如何根据业务领域进行服务拆分，理解了"高内聚低耦合"在微服务中的体现
- 掌握了 DDD 限界上下文的概念，能够判断哪些业务应该放在一起、哪些应该拆分
- 认识到服务边界划分是微服务架构中最关键的决策

### 6.2.2 Spring Cloud Alibaba 生态

- **Nacos**: 服务注册与发现机制，临时/永久实例的区别，健康检查
- **OpenFeign**: 声明式 HTTP 调用，负载均衡集成
- **Gateway**: 路由谓词工厂、过滤器链、响应式编程模型
- **Resilience4j**: 熔断器的状态机 (CLOSED → OPEN → HALF_OPEN)，滑动窗口统计

### 6.2.3 分布式事务与 Seata

- 理解了 AT 模式的工作原理: 解析 SQL → 生成前后镜像 → 写入 undo_log → 二阶段提交/回滚
- 解决了 Seata + Spring Cloud Gateway 的集成问题
- 在没有分布式事务的场景下，理解了最终一致性的重要性

### 6.2.4 消息队列与异步解耦

- RabbitMQ 的 Exchange/Binding/Queue 模型
- 订单创建后 MQ 通知支付服务，将同步调用改为异步，降低了下单接口的响应时间

## 6.3 攻克的技术问题

### 问题1: Spring Cloud Alibaba 版本兼容性

**现象**: 引入依赖后启动报错，Bean 无法创建。

**解决**: 查阅 Spring Cloud Alibaba 官方版本说明，Spring Boot 3.4.x 需配套 Spring Cloud Alibaba 2023.0.x，不能使用 2022.x 版本。最终确定版本组合: Boot 3.4.5 + Cloud 2024.0.1 + Alibaba 2023.0.3.2。

### 问题2: Seata AT 模式回滚不生效

**现象**: Feign 调用 Product Service 扣库存成功后，Order Service 业务异常，但库存未回滚。

**解决**: 需要在调用方（order-service）和被调用方（product-service）都配置 Seata，并且两个服务使用同一个事务分组。@GlobalTransactional 注解要加在入口方法上，且方法必须为 public。

### 问题3: MQ 消息序列化

**现象**: Order Service 发送 Order 对象到 MQ，Payment Service 消费时反序列化失败。

**解决**: 添加 Serializable 接口，并在消费端使用相同的类路径和字段结构。最终通过 DTO 模式解决跨服务的消息契约问题。

## 6.4 感悟与展望

### 对微服务架构的感悟

微服务不是银弹。对于小型项目来说，微服务引入的复杂度（服务发现、分布式事务、消息队列、链路追踪）远大于单体架构。但通过这个项目，我深刻理解了"为什么需要微服务"——当系统规模增长到一定级别，**独立部署、故障隔离、弹性扩缩**这些能力会成为刚需。

### 对项目开发的展望

1. **可观测性增强**: 集成 Micrometer + Prometheus + Grafana 监控，接入 SkyWalking 链路追踪
2. **CI/CD**: 增加 GitHub Actions 自动化构建和部署
3. **Kubernetes**: 将 Docker Compose 迁移到 K8s，使用 HPA 自动扩缩容
4. **前端实现**: 完成 Vue 3 前端开发，对接 Gateway 接口

## 6.5 源码访问

GitHub: (待上传)

---

*项目时间: 2026年7月*
*开发环境: Java 17 + Spring Boot 3.4.x + Spring Cloud Alibaba 2023.0.x*
