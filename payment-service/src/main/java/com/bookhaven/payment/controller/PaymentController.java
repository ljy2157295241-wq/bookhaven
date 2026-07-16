package com.bookhaven.payment.controller;

import com.bookhaven.common.model.CommonResult;
import com.bookhaven.payment.entity.Payment;
import com.bookhaven.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public CommonResult<Payment> createPayment(
            @RequestParam String orderNo,
            @RequestParam Long orderId,
            @RequestParam java.math.BigDecimal amount) {
        return CommonResult.success(paymentService.createPayment(orderNo, orderId, amount));
    }

    @PostMapping("/{paymentNo}/pay")
    public CommonResult<Payment> pay(@PathVariable String paymentNo) {
        return CommonResult.success(paymentService.pay(paymentNo));
    }

    @PostMapping("/{paymentNo}/refund")
    public CommonResult<Payment> refund(@PathVariable String paymentNo) {
        return CommonResult.success(paymentService.refund(paymentNo));
    }
}
