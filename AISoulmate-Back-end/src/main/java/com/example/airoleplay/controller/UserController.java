package com.example.airoleplay.controller;

import com.example.airoleplay.dto.LoginRequest;
import com.example.airoleplay.dto.RegisterRequest;
import com.example.airoleplay.dto.UpdatePasswordRequest;
import com.example.airoleplay.dto.UserResponse;
import com.example.airoleplay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest req) {
        return userService.register(req);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest req) {
        return userService.login(req);
    }

    @PostMapping("/updatePassword")
    public UserResponse updatePassword(@RequestBody UpdatePasswordRequest req) {
        return userService.updatePassword(req);
    }
}
