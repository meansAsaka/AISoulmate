package com.example.airoleplay.service.impl;

import com.example.airoleplay.dto.LoginRequest;
import com.example.airoleplay.dto.RegisterRequest;
import com.example.airoleplay.dto.UserResponse;
import com.example.airoleplay.entity.User;
import com.example.airoleplay.repository.UserRepository;
import com.example.airoleplay.service.UserService;
import com.example.airoleplay.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OSSService ossService;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已注册");
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getNickname());
        if (req.getAvatar() != null && req.getAvatar().startsWith("data:image")) {
            // Extract the base64 part (remove the prefix "data:image/jpeg;base64,")
            String base64Image = req.getAvatar().split(",")[1];
            // Upload to OSS and get the URL
            String avatarUrl = ossService.uploadImage(base64Image);
            user.setAvatar(avatarUrl);
        }
        userRepository.save(user);
        return toResponse(user);
    }

    public UserResponse login(LoginRequest req) {
        Optional<User> userOpt = userRepository.findByEmail(req.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("密码错误");
        }
        UserResponse resp = toResponse(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        resp.setToken(token);
        return resp;
    }

    private UserResponse toResponse(User user) {
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setNickname(user.getNickname());
        resp.setAvatar(user.getAvatar());
        resp.setCreatedAt(user.getCreatedAt());
        return resp;
    }
}
