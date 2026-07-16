package com.bookhaven.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_EXCHANGE = "bookhaven.payment.direct";
    public static final String PAYMENT_QUEUE = "bookhaven.payment.queue";
    public static final String PAYMENT_ROUTING_KEY = "payment.create";

    public static final String ORDER_STATUS_EXCHANGE = "bookhaven.order.status";
    public static final String ORDER_STATUS_QUEUE = "bookhaven.order.status.queue";
    public static final String ORDER_STATUS_ROUTING_KEY = "order.status";

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE).build();
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue()).to(paymentExchange()).with(PAYMENT_ROUTING_KEY);
    }

    @Bean
    public DirectExchange orderStatusExchange() {
        return new DirectExchange(ORDER_STATUS_EXCHANGE);
    }

    @Bean
    public Queue orderStatusQueue() {
        return QueueBuilder.durable(ORDER_STATUS_QUEUE).build();
    }

    @Bean
    public Binding orderStatusBinding() {
        return BindingBuilder.bind(orderStatusQueue()).to(orderStatusExchange()).with(ORDER_STATUS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
