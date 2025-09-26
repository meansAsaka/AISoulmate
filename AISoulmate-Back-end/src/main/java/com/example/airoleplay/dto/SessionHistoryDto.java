package com.example.airoleplay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionHistoryDto {
    private String sessionId;
    private String avatarUrl;
    private String latestMessageText;
}

