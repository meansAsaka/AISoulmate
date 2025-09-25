package com.example.airoleplay.service;

import com.example.airoleplay.dto.LoginRequest;
import com.example.airoleplay.dto.RegisterRequest;
import com.example.airoleplay.dto.UpdatePasswordRequest;
import com.example.airoleplay.dto.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest req);

    UserResponse login(LoginRequest req);

    UserResponse updatePassword(UpdatePasswordRequest req);
}
