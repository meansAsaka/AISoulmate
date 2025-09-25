package com.example.airoleplay.service;

import com.example.airoleplay.entity.Session;
import org.springframework.web.socket.WebSocketSession;

public interface LlmService {
    void streamChat(String text, String sessionId, WebSocketSession webSocketSession);
    String generateResponse(String text);
    String generateResponse(String text, Session session);
}