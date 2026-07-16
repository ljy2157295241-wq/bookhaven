package com.bookhaven.order.controller;

import com.bookhaven.common.model.CommonResult;
import com.bookhaven.order.entity.Order;
import com.bookhaven.order.entity.OrderItem;
import com.bookhaven.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public CommonResult<Order> createOrder(
            @RequestHeader("userId") Long userId,
            @RequestParam("cartItemIds") List<Long> cartItemIds,
            @RequestParam String receiverName,
            @RequestParam String receiverPhone,
            @RequestParam String receiverAddress,
            @RequestParam(required = false) String remark) {
        return CommonResult.success(orderService.createOrder(
                userId, cartItemIds, receiverName, receiverPhone, receiverAddress, remark));
    }

    @GetMapping("/{id}")
    public CommonResult<Order> getOrderById(@PathVariable Long id) {
        return CommonResult.success(orderService.getOrderById(id));
    }

    @GetMapping
    public CommonResult<List<Order>> getUserOrders(@RequestHeader("userId") Long userId) {
        return CommonResult.success(orderService.getUserOrders(userId));
    }

    @GetMapping("/{id}/items")
    public CommonResult<List<OrderItem>> getOrderItems(@PathVariable Long id) {
        return CommonResult.success(orderService.getOrderItems(id));
    }

    @PostMapping("/{id}/cancel")
    public CommonResult<Boolean> cancelOrder(
            @PathVariable Long id,
            @RequestHeader("userId") Long userId) {
        return CommonResult.success(orderService.cancelOrder(id, userId));
    }

    // 内部Feign调用端点 - 支付成功回调
    @PostMapping("/payment-success")
    public CommonResult<Boolean> paymentSuccess(
            @RequestParam String orderNo,
            @RequestParam String paymentNo) {
        return CommonResult.success(orderService.processPaymentSuccess(orderNo, paymentNo));
    }
}
