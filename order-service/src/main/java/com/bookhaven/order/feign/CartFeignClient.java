package com.bookhaven.order.feign;

import com.bookhaven.common.model.CommonResult;
import com.bookhaven.order.entity.CartItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "cart-service", path = "/api/cart")
public interface CartFeignClient {

    @PostMapping("/selected")
    CommonResult<List<CartItemDTO>> getSelectedItems(
            @RequestHeader("userId") Long userId,
            @RequestBody List<Long> ids);

    @PostMapping("/clear-checked")
    CommonResult<Void> clearCheckedItems(
            @RequestHeader("userId") Long userId,
            @RequestBody List<Long> ids);
}
