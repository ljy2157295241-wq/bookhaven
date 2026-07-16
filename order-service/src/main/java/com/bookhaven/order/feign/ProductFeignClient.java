package com.bookhaven.order.feign;

import com.bookhaven.common.model.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", path = "/api")
public interface ProductFeignClient {

    @PostMapping("/products/{id}/deduct")
    CommonResult<Boolean> deductStock(@PathVariable("id") Long bookId,
                                       @RequestParam("quantity") Integer quantity);

    @PostMapping("/products/{id}/restore")
    CommonResult<Boolean> restoreStock(@PathVariable("id") Long bookId,
                                        @RequestParam("quantity") Integer quantity);
}
