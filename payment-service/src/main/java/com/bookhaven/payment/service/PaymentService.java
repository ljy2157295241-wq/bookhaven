package com.bookhaven.payment.service;

import com.bookhaven.common.exception.BusinessException;
import com.bookhaven.payment.entity.Payment;
import com.bookhaven.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.bookhaven.payment.config.RabbitMQConfig.ORDER_STATUS_EXCHANGE;
import static com.bookhaven.payment.config.RabbitMQConfig.ORDER_STATUS_ROUTING_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Payment createPayment(String orderNo, Long orderId, BigDecimal amount) {
        // 幂等性校验：防止重复创建支付单
        Payment existing = paymentRepository.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Payment>()
                        .eq(Payment::getOrderNo, orderNo)
                        .eq(Payment::getStatus, "PENDING"));
        if (existing != null) {
            log.info("Payment already exists for order: {}", orderNo);
            return existing;
        }

        Payment payment = new Payment();
        payment.setPaymentNo("PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        payment.setOrderNo(orderNo);
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setPayMethod("MOCK");
        paymentRepository.insert(payment);
        log.info("Payment created: {}", payment.getPaymentNo());
        return payment;
    }

    /**
     * 模拟支付（MOCK 模式）
     * 实际项目中对接支付宝/微信 SDK
     */
    @Transactional
    public Payment pay(String paymentNo) {
        Payment payment = paymentRepository.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Payment>()
                        .eq(Payment::getPaymentNo, paymentNo));
        if (payment == null) {
            throw new BusinessException("Payment not found");
        }
        if (!"PENDING".equals(payment.getStatus())) {
            log.info("Payment {} already processed, skip", paymentNo);
            return payment;
        }

        // 模拟支付处理
        payment.setStatus("SUCCESS");
        payment.setPayTime(LocalDateTime.now());
        paymentRepository.updateById(payment);
        log.info("Payment success: {}", paymentNo);

        // 发送 MQ 消息通知 Order Service
        Map<String, Object> message = new HashMap<>();
        message.put("orderNo", payment.getOrderNo());
        message.put("paymentNo", payment.getPaymentNo());
        message.put("status", "SUCCESS");
        rabbitTemplate.convertAndSend(ORDER_STATUS_EXCHANGE, ORDER_STATUS_ROUTING_KEY, message);
        log.info("Payment success message sent to MQ for order: {}", payment.getOrderNo());

        return payment;
    }

    @Transactional
    public Payment refund(String paymentNo) {
        Payment payment = paymentRepository.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Payment>()
                        .eq(Payment::getPaymentNo, paymentNo));
        if (payment == null || !"SUCCESS".equals(payment.getStatus())) {
            throw new BusinessException("Payment cannot be refunded");
        }
        payment.setStatus("REFUNDED");
        paymentRepository.updateById(payment);

        Map<String, Object> message = new HashMap<>();
        message.put("orderNo", payment.getOrderNo());
        message.put("paymentNo", payment.getPaymentNo());
        message.put("status", "REFUNDED");
        rabbitTemplate.convertAndSend(ORDER_STATUS_EXCHANGE, ORDER_STATUS_ROUTING_KEY, message);

        return payment;
    }
}
