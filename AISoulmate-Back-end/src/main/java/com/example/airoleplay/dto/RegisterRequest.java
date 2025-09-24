package com.example.airoleplay.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String nickname;
    private String avatar;
}

