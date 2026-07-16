package com.bookhaven.product.controller;

import com.bookhaven.common.model.CommonResult;
import com.bookhaven.common.model.PageDTO;
import com.bookhaven.product.entity.Book;
import com.bookhaven.product.entity.Category;
import com.bookhaven.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public CommonResult<PageDTO<Book>> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return CommonResult.success(productService.searchBooks(keyword, categoryId, page, size));
    }

    @GetMapping("/products/{id}")
    public CommonResult<Book> getBookById(@PathVariable Long id) {
        return CommonResult.success(productService.getBookById(id));
    }

    @PostMapping("/products")
    public CommonResult<Book> addBook(@RequestBody Book book) {
        return CommonResult.success(productService.addBook(book));
    }

    @PutMapping("/products/{id}")
    public CommonResult<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        return CommonResult.success(productService.updateBook(book));
    }

    // 内部Feign调用端点 - 扣减库存
    @PostMapping("/products/{id}/deduct")
    public CommonResult<Boolean> deductStock(@PathVariable Long id, @RequestParam Integer quantity) {
        return CommonResult.success(productService.deductStock(id, quantity));
    }

    // 内部Feign调用端点 - 恢复库存
    @PostMapping("/products/{id}/restore")
    public CommonResult<Boolean> restoreStock(@PathVariable Long id, @RequestParam Integer quantity) {
        return CommonResult.success(productService.restoreStock(id, quantity));
    }

    @GetMapping("/categories")
    public CommonResult<List<Category>> getAllCategories() {
        return CommonResult.success(productService.getAllCategories());
    }
}
