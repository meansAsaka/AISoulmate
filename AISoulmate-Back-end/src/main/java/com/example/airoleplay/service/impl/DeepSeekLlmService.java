package com.example.airoleplay.service.impl;

import com.example.airoleplay.service.LlmService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.airoleplay.util.PromptBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekLlmService implements LlmService {
    private final SessionService sessionService;
    private final CharacterService characterService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${llm.deepseek.api-key}")
    private String apiKey;

    @Override
    public void streamChat(String text, String sessionId, WebSocketSession webSocketSession) {
        try {
            // 保存用户消息
            sessionService.saveMessage(sessionId, com.example.airoleplay.entity.Message.Role.user, text);
            sessionService.saveMessagesToRedis(sessionId, text);

            // 获取 Session 实体
            com.example.airoleplay.entity.Session session = sessionService.getSessionById(sessionId).orElse(null);
            if (session == null) {
                log.error("未找到会话: {}", sessionId);
                return;
            }

            // 获取历史消息
            List<String> historytexts = sessionService.getMessagesFromRedis(session.getId());

            // 构建带人设的 prompt
            String prompt = PromptBuilder.buildRolePlayPrompt(text, session, characterService, historytexts);

            // 调用真实API
            String response = callDeepSeekApi(prompt);
            sessionService.saveMessage(sessionId, com.example.airoleplay.entity.Message.Role.assistant, response);
            sessionService.saveMessagesToRedis(sessionId, response);

            // 发送完整响应
            synchronized (webSocketSession) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("reply", response);
                msg.put("done", true);
                webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            }

        } catch (Exception e) {
            log.error("DeepSeek API调用失败", e);
        }
    }

    private String callDeepSeekApi(String text) throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "deepseek-chat");
        request.put("messages", List.of(Map.of("role", "user", "content", text)));
        request.put("stream", false);
        request.put("max_tokens", 1024);

        WebClient client = WebClient.builder()
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Accept", "application/json")
            .build();

        String response = client.post()
            .uri("https://api.deepseek.com/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        log.info("DeepSeek API响应: {}", response);
        JsonNode node = objectMapper.readTree(response);

        // 检查是否有错误
        if (node.has("error")) {
            throw new RuntimeException("API调用失败: " + node.path("error").path("message").asText());
        }

        JsonNode choices = node.path("choices");
        String result = "DeepSeek回复：" + text;
        if (choices.isArray() && choices.size() > 0) {
            String content = choices.get(0).path("message").path("content").asText();
            if (!content.isEmpty()) {
                result = content;
            }
        }
        // 返回 JSON 字符串
        Map<String, Object> msg = new HashMap<>();
        msg.put("reply", result);
        msg.put("done", true);
        return objectMapper.writeValueAsString(msg);
    }
    
    @Override
    public String generateResponse(String text) {
        try {
            return callDeepSeekApi(text);
        } catch (Exception e) {
            log.error("DeepSeek API调用失败", e);
            throw new RuntimeException("DeepSeek API调用失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String generateResponse(String text, com.example.airoleplay.entity.Session session) {
        try {
            List<String> historytexts = sessionService.getMessagesFromRedis(session.getId());
            String prompt = PromptBuilder.buildRolePlayPrompt(text, session, characterService, historytexts);
            return callDeepSeekApi(prompt);
        } catch (Exception e) {
            log.error("DeepSeek API调用失败", e);
            throw new RuntimeException("DeepSeek API调用失败: " + e.getMessage(), e);
        }
    }
}
