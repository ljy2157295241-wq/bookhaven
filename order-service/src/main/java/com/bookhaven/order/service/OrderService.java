package com.bookhaven.order.service;

import com.bookhaven.common.exception.BusinessException;
import com.bookhaven.order.entity.*;
import com.bookhaven.order.feign.CartFeignClient;
import com.bookhaven.order.feign.ProductFeignClient;
import com.bookhaven.order.repository.OrderItemRepository;
import com.bookhaven.order.repository.OrderLogRepository;
import com.bookhaven.order.repository.OrderRepository;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.bookhaven.order.config.RabbitMQConfig.PAYMENT_EXCHANGE;
import static com.bookhaven.order.config.RabbitMQConfig.PAYMENT_ROUTING_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderLogRepository orderLogRepository;
    private final ProductFeignClient productFeignClient;
    private final CartFeignClient cartFeignClient;
    private final RabbitTemplate rabbitTemplate;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;

    /**
     * 核心跨服务业务流程：创建订单
     * 涉及 Order Service -> Cart Service -> Product Service -> Payment Service 的跨服务调用
     * 使用 Seata 分布式事务保证一致性
     */
    @GlobalTransactional(name = "create-order-tx", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, List<Long> cartItemIds,
                             String receiverName, String receiverPhone,
                             String receiverAddress, String remark) {
        log.info("=== Starting order creation for user: {} ===", userId);

        // 1. 调用 Cart Service 获取购物车选中的商品（跨服务调用）
        List<CartItemDTO> cartItems = cartFeignClient.getSelectedItems(userId, cartItemIds).getData();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException("Cart is empty or items not found");
        }
        log.info("Fetched {} items from cart service", cartItems.size());

        // 2. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus("PENDING_PAY");
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setRemark(remark);

        BigDecimal total = cartItems.stream()
                .map(i -> i.getBookPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        orderRepository.insert(order);
        log.info("Order created: {}", order.getOrderNo());

        // 3. 保存订单项 & 调用 Product Service 扣减库存（跨服务调用 + 分布式事务）
        for (CartItemDTO cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setBookId(cartItem.getBookId());
            item.setBookTitle(cartItem.getBookTitle());
            item.setBookCover(cartItem.getBookCover());
            item.setBookPrice(cartItem.getBookPrice());
            item.setQuantity(cartItem.getQuantity());
            orderItemRepository.insert(item);

            // 调用 Product Service 扣减库存（跨服务调用）
            // 使用 Resilience4j 熔断保护
            Boolean deductResult = circuitBreakerFactory.create("product-service")
                    .run(() -> productFeignClient.deductStock(cartItem.getBookId(), cartItem.getQuantity()).getData(),
                            throwable -> {
                                log.error("Failed to deduct stock for book {}, circuit breaker triggered", cartItem.getBookId(), throwable);
                                throw new BusinessException("Stock service unavailable, order cancelled");
                            });
            if (!Boolean.TRUE.equals(deductResult)) {
                throw new BusinessException("Insufficient stock for book: " + cartItem.getBookTitle());
            }
        }
        log.info("Stock deducted successfully");

        // 4. 写入订单日志
        OrderLog logEntry = new OrderLog();
        logEntry.setOrderId(order.getId());
        logEntry.setFromStatus(null);
        logEntry.setToStatus("PENDING_PAY");
        logEntry.setRemark("Order created");
        orderLogRepository.insert(logEntry);

        // 5. 清空购物车中的已购商品（跨服务调用）
        cartFeignClient.clearCheckedItems(userId, cartItemIds);
        log.info("Cart cleared for checked items");

        // 6. 发送 MQ 消息给 Payment Service 创建支付单（异步解耦）
        rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, PAYMENT_ROUTING_KEY, order);
        log.info("Payment message sent to MQ for order: {}", order.getOrderNo());

        return order;
    }

    /**
     * 幂等处理：支付成功回调
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean processPaymentSuccess(String orderNo, String paymentNo) {
        Order order = orderRepository.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo));
        if (order == null) {
            log.warn("Order not found for payment callback: {}", orderNo);
            return false;
        }
        // 幂等性校验：已支付的订单不再处理
        if (!"PENDING_PAY".equals(order.getStatus())) {
            log.info("Order {} already processed, skip payment callback", orderNo);
            return true;
        }
        order.setStatus("PAID");
        order.setPayTime(LocalDateTime.now());
        orderRepository.updateById(order);

        OrderLog logEntry = new OrderLog();
        logEntry.setOrderId(order.getId());
        logEntry.setFromStatus("PENDING_PAY");
        logEntry.setToStatus("PAID");
        logEntry.setRemark("Payment success, paymentNo: " + paymentNo);
        orderLogRepository.insert(logEntry);
        log.info("Order {} paid successfully", orderNo);
        return true;
    }

    /**
     * 取消订单（含恢复库存的跨服务调用）
     */
    @GlobalTransactional(name = "cancel-order-tx", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("Order not found");
        }
        if (!"PENDING_PAY".equals(order.getStatus())) {
            throw new BusinessException("Order cannot be cancelled in current status");
        }

        // 恢复库存
        List<OrderItem> items = orderItemRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            productFeignClient.restoreStock(item.getBookId(), item.getQuantity());
        }

        order.setStatus("CANCELLED");
        order.setCancelTime(LocalDateTime.now());
        orderRepository.updateById(order);

        OrderLog logEntry = new OrderLog();
        logEntry.setOrderId(order.getId());
        logEntry.setFromStatus("PENDING_PAY");
        logEntry.setToStatus("CANCELLED");
        logEntry.setRemark("Order cancelled by user");
        orderLogRepository.insert(logEntry);
        return true;
    }

    public Order getOrderById(Long id) {
        return orderRepository.selectById(id);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime));
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId));
    }

    private String generateOrderNo() {
        return "BH" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
