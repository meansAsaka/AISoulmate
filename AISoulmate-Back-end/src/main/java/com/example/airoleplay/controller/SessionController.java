package com.example.airoleplay.controller;

import com.example.airoleplay.dto.CreateSessionRequest;
import com.example.airoleplay.entity.Message;
import com.example.airoleplay.entity.Session;
import com.example.airoleplay.service.impl.LlmServiceFactory;
import com.example.airoleplay.service.impl.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final LlmServiceFactory llmServiceFactory;

    @PostMapping
    public ResponseEntity<Session> createSession(@RequestBody CreateSessionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Session session = sessionService.createSession(userId, request);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Session> getSession(@PathVariable String id) {
        return sessionService.getSessionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<Message>> getSessionMessages(@PathVariable String id) {
        List<Message> messages = sessionService.getSessionMessages(id);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<String> sendMessage(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("消息内容不能为空");
            }

            // 保存用户消息
            sessionService.saveMessage(id, Message.Role.user, text);
            sessionService.saveMessagesToRedis(id, text);

            // 获取会话信息
            Session session = sessionService.getSessionById(id).orElse(null);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }

            // 调用LLM服务生成回复，传入会话信息
            String response = llmServiceFactory.getService(session.getModelName()).generateResponse(text, session);
            
            // 保存AI回复
            sessionService.saveMessage(id, Message.Role.assistant, response);
            sessionService.saveMessagesToRedis(id, response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("处理消息失败: " + e.getMessage());
        }
    }
}