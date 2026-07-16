package com.bookhaven.order.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4jConfig {
    // Resilience4j is configured via application.yml
    // The auto-configuration will create the CircuitBreakerFactory bean
}
