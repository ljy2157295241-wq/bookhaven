package com.bookhaven.payment.consumer;

import com.bookhaven.payment.entity.Payment;
import com.bookhaven.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCreateConsumer {

    private final PaymentService paymentService;

    @RabbitListener(queues = "bookhaven.payment.queue")
    public void handlePaymentCreate(OrderDTO order) {
        log.info("Received payment create message for order: {}", order.getOrderNo());
        Payment payment = paymentService.createPayment(order.getOrderNo(), order.getId(), order.getTotalAmount());
        log.info("Payment created: {}", payment.getPaymentNo());
    }
}
