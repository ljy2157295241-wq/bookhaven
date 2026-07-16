package com.bookhaven.cart.feign;

import com.bookhaven.common.model.CommonResult;
import com.bookhaven.cart.entity.CartItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", path = "/api")
public interface ProductFeignClient {
    @GetMapping("/products/{id}")
    CommonResult<CartItem> getProduct(@PathVariable("id") Long id);
}
