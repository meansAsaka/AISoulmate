package com.example.airoleplay.service;

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

            // 获取 Session 实体
            com.example.airoleplay.entity.Session session = sessionService.getSessionById(sessionId).orElse(null);
            if (session == null) {
                log.error("未找到会话: {}", sessionId);
                return;
            }
            
            // 构建请求
            Map<String, Object> request = new HashMap<>();
            request.put("model", "deepseek-chat");
            request.put("messages", List.of(Map.of("role", "user", "content", text)));
            request.put("stream", true);

            WebClient client = WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

            StringBuilder fullResponse = new StringBuilder();

            // 构建带人设的 prompt
            String prompt = buildRolePlayPrompt(text, session);
            
            // 调用真实API
            String response = callDeepSeekApi(prompt);
            sessionService.saveMessage(sessionId, com.example.airoleplay.entity.Message.Role.assistant, response);
            
            // 发送完整响应
            synchronized (webSocketSession) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("delta", response);
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
        if (choices.isArray() && choices.size() > 0) {
            String result = choices.get(0).path("message").path("content").asText();
            return result.isEmpty() ? "DeepSeek回复：" + text : result;
        }
        return "DeepSeek回复：" + text;
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
            String prompt = buildRolePlayPrompt(text, session);
            return callDeepSeekApi(prompt);
        } catch (Exception e) {
            log.error("DeepSeek API调用失败", e);
            throw new RuntimeException("DeepSeek API调用失败: " + e.getMessage(), e);
        }
    }
    
    private String buildRolePlayPrompt(String userText, com.example.airoleplay.entity.Session session) {
        StringBuilder prompt = new StringBuilder();
        
        // 获取角色信息
        characterService.getCharacterById(session.getCharacterId())
            .ifPresent(character -> {
                prompt.append("你现在要扮演角色：").append(character.getName()).append("\n");
                if (character.getBrief() != null) {
                    prompt.append("角色描述：").append(character.getBrief()).append("\n");
                }
            });
        
        // 根据模式添加指导
        switch (session.getMode()) {
            case immersive:
                prompt.append("请完全沉浸在这个角色中，用第一人称回答，保持角色的性格特点和说话方式。\n");
                break;
            case academic:
                prompt.append("请以学术讨论的方式，结合角色的知识背景来回答问题。\n");
                break;
            case socratic:
                prompt.append("请用苏格拉底式的问答方法，通过提问来引导思考。\n");
                break;
        }
        
        prompt.append("\n用户问题：").append(userText);
        
        return prompt.toString();
    }
}