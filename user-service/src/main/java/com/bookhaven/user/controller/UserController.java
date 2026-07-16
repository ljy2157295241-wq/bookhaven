package com.bookhaven.user.controller;

import com.bookhaven.common.model.CommonResult;
import com.bookhaven.user.entity.User;
import com.bookhaven.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public CommonResult<Map<String, Object>> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String email) {
        return CommonResult.success(userService.register(username, password, email));
    }

    @PostMapping("/login")
    public CommonResult<Map<String, Object>> login(
            @RequestParam String username,
            @RequestParam String password) {
        return CommonResult.success(userService.login(username, password));
    }

    @GetMapping("/{id}")
    public CommonResult<User> getUserById(@PathVariable Long id) {
        return CommonResult.success(userService.getUserById(id));
    }

    @GetMapping("/info")
    public CommonResult<User> getUserInfo(@RequestHeader("Authorization") String token) {
        // token parsing handled by JwtInterceptor or Gateway
        return CommonResult.success(userService.getUserById(1L));
    }
}
