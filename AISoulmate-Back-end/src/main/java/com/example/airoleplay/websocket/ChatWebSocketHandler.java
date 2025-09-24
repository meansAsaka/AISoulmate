package com.example.airoleplay.websocket;

import com.example.airoleplay.dto.ChatMessage;
import com.example.airoleplay.entity.Session;
import com.example.airoleplay.service.LlmService;
import com.example.airoleplay.service.impl.LlmServiceFactory;
import com.example.airoleplay.service.impl.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final SessionService sessionService;
    private final LlmServiceFactory llmServiceFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = getSessionIdFromUri(session.getUri());
        if (sessionId == null) {
            session.close(CloseStatus.BAD_DATA.withReason("Missing sessionId parameter"));
            return;
        }
        
        Optional<Session> chatSession = sessionService.getSessionById(sessionId);
        if (chatSession.isEmpty()) {
            session.close(CloseStatus.BAD_DATA.withReason("Invalid sessionId"));
            return;
        }
        
        log.info("WebSocket连接建立，sessionId: {}", sessionId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = getSessionIdFromUri(session.getUri());
        if (sessionId == null) {
            return;
        }

        try {
            ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
            
            Optional<Session> chatSession = sessionService.getSessionById(sessionId);
            if (chatSession.isEmpty()) {
                return;
            }

            String modelName = chatSession.get().getModelName();
            LlmService llmService = llmServiceFactory.getService(modelName);
            
            // 异步处理LLM调用
            new Thread(() -> llmService.streamChat(chatMessage.getText(), sessionId, session)).start();
            
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket连接关闭，状态: {}", closeStatus);
    }

    private String getSessionIdFromUri(URI uri) {
        if (uri == null) return null;
        return UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("sessionId");
    }
}