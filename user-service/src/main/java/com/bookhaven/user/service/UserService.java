package com.bookhaven.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookhaven.common.exception.BusinessException;
import com.bookhaven.user.config.JwtConfig;
import com.bookhaven.user.entity.User;
import com.bookhaven.user.repository.UserRepository;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;

    public Map<String, Object> register(String username, String password, String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username);
        if (userRepository.selectOne(queryWrapper) != null) {
            throw new BusinessException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtil.sha256Hex(password));
        user.setEmail(email);
        user.setNickname(username);
        userRepository.insert(user);

        String token = jwtConfig.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        return result;
    }

    public Map<String, Object> login(String username, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username);
        User user = userRepository.selectOne(queryWrapper);
        if (user == null || !user.getPassword().equals(DigestUtil.sha256Hex(password))) {
            throw new BusinessException(401, "Invalid username or password");
        }
        String token = jwtConfig.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        return result;
    }

    public User getUserById(Long id) {
        return userRepository.selectById(id);
    }

    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username);
        return userRepository.selectOne(queryWrapper);
    }
}
