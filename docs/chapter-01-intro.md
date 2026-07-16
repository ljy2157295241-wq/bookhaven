# 第1章 目标系统功能简介

## 1.1 系统概述

BookHaven 是一个基于微服务架构的 B2C 在线图书商城系统。系统提供图书浏览搜索、购物车管理、订单提交与支付等完整的电商核心功能，旨在为用户提供便捷、流畅的在线购书体验。

## 1.2 用户角色

系统定义两类主要用户角色：

**普通用户 (User)**
- 浏览和搜索图书
- 查看图书详情
- 管理购物车
- 提交和管理订单
- 模拟支付
- 管理收货地址

**管理员 (Admin)**
- 图书上下架管理
- 库存调整
- 订单处理
- 分类管理

## 1.3 核心用例

| 用例编号 | 用例名称 | 参与角色 | 涉及微服务 |
|---|---|---|---|
| UC-01 | 用户注册 | 普通用户 | user-service |
| UC-02 | 用户登录 | 普通用户 | user-service |
| UC-03 | 浏览图书 | 普通用户 | product-service |
| UC-04 | 搜索图书 | 普通用户 | product-service |
| UC-05 | 加入购物车 | 普通用户 | cart-service, product-service |
| UC-06 | 查看购物车 | 普通用户 | cart-service |
| UC-07 | 提交订单 | 普通用户 | order-service, cart-service, product-service, payment-service |
| UC-08 | 取消订单 | 普通用户 | order-service, product-service |
| UC-09 | 模拟支付 | 普通用户 | payment-service, order-service |
| UC-10 | 图书管理 | 管理员 | product-service |

## 1.4 核心业务流程: 下单支付

这是系统中最重要的跨服务业务流程：

```
1. 用户浏览图书 → 将图书加入购物车
2. 用户在购物车页面选择商品 → 点击"提交订单"
3. 系统调用 Cart Service 获取购物车商品信息
4. 系统调用 Product Service 扣减库存
5. 系统创建订单（状态: 待支付）
6. 系统通过 RabbitMQ 异步通知 Payment Service 创建支付单
7. 用户点击"支付" → Payment Service 处理支付
8. 支付成功后，Payment Service 通过 MQ 通知 Order Service
9. Order Service 更新订单状态为"已支付"
10. 用户可在订单列表查看订单状态
```
