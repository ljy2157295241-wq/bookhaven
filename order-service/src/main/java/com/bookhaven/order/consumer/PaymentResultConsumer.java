package com.bookhaven.order.consumer;

import com.bookhaven.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = "bookhaven.order.status.queue")
    public void handlePaymentResult(Map<String, Object> message) {
        String orderNo = (String) message.get("orderNo");
        String paymentNo = (String) message.get("paymentNo");
        String status = (String) message.get("status");
        log.info("Received payment result: orderNo={}, paymentNo={}, status={}", orderNo, paymentNo, status);

        if ("SUCCESS".equals(status)) {
            orderService.processPaymentSuccess(orderNo, paymentNo);
        }
    }
}
