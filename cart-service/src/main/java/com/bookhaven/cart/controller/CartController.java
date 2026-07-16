package com.bookhaven.cart.controller;

import com.bookhaven.cart.entity.CartItem;
import com.bookhaven.cart.service.CartService;
import com.bookhaven.common.model.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public CommonResult<List<CartItem>> getCart(@RequestHeader("userId") Long userId) {
        return CommonResult.success(cartService.getCartItems(userId));
    }

    @PostMapping("/items")
    public CommonResult<CartItem> addItem(
            @RequestHeader("userId") Long userId,
            @RequestBody CartItem item) {
        return CommonResult.success(cartService.addItem(
                userId, item.getBookId(), item.getBookTitle(),
                item.getBookCover(), item.getBookPrice(), item.getQuantity()));
    }

    @PutMapping("/items/{id}")
    public CommonResult<Void> updateQuantity(
            @PathVariable Long id, @RequestParam Integer quantity) {
        cartService.updateQuantity(id, quantity);
        return CommonResult.success();
    }

    @DeleteMapping("/items/{id}")
    public CommonResult<Void> removeItem(@PathVariable Long id) {
        cartService.removeItem(id);
        return CommonResult.success();
    }

    @DeleteMapping("/clear")
    public CommonResult<Void> clearCart(@RequestHeader("userId") Long userId) {
        cartService.clearCart(userId);
        return CommonResult.success();
    }

    // 内部Feign调用 - 获取选中的购物车项(供OrderService下单时使用)
    @PostMapping("/selected")
    public CommonResult<List<CartItem>> getSelectedItems(
            @RequestHeader("userId") Long userId,
            @RequestBody List<Long> ids) {
        return CommonResult.success(cartService.getSelectedItems(userId, ids));
    }

    // 内部Feign调用 - 下单后清空购物车
    @PostMapping("/clear-checked")
    public CommonResult<Void> clearCheckedItems(
            @RequestHeader("userId") Long userId,
            @RequestBody List<Long> ids) {
        ids.forEach(cartService::removeItem);
        return CommonResult.success();
    }
}
