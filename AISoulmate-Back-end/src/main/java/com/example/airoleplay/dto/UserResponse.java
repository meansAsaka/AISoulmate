package com.example.airoleplay.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private String id;
    private String email;
    private String nickname;
    private String avatar;
    private LocalDateTime createdAt;
    private String token;
}
