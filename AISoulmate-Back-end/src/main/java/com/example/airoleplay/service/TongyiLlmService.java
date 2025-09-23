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
public class TongyiLlmService implements LlmService {
    private final SessionService sessionService;
    private final CharacterService characterService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${llm.tongyi.api-key}")
    private String apiKey;

    @Override
    public void streamChat(String text, String sessionId, WebSocketSession webSocketSession) {
        try {
            // 保存用户消息
            sessionService.saveMessage(sessionId, com.example.airoleplay.entity.Message.Role.user, text);
            
            // 构建请求
            Map<String, Object> request = new HashMap<>();
            request.put("model", "qwen-turbo");
            
            Map<String, Object> input = new HashMap<>();
            input.put("messages", List.of(Map.of("role", "user", "content", text)));
            request.put("input", input);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("incremental_output", true);
            request.put("parameters", parameters);

            WebClient client = WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

            StringBuilder fullResponse = new StringBuilder();
            
            // 调用真实API
            String response = callTongyiApi(text);
            sessionService.saveMessage(sessionId, com.example.airoleplay.entity.Message.Role.assistant, response);
            
            // 发送完整响应
            synchronized (webSocketSession) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("delta", response);
                msg.put("done", true);
                webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            }
                
        } catch (Exception e) {
            log.error("通义千问API调用失败", e);
        }
    }
    
    private String callTongyiApi(String text) throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "qwen-turbo");
        
        Map<String, Object> input = new HashMap<>();
        input.put("messages", List.of(Map.of("role", "user", "content", text)));
        request.put("input", input);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("result_format", "message");
        request.put("parameters", parameters);
        
        WebClient client = WebClient.builder()
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Accept", "application/json")
            .build();
            
        String response = client.post()
            .uri("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String.class)
            .block();
            
        log.info("通义千问API响应: {}", response);
        JsonNode node = objectMapper.readTree(response);
        
        // 检查是否有错误
        if (node.has("code")) {
            throw new RuntimeException("API调用失败: " + node.path("message").asText());
        }
        
        String result = node.path("output").path("choices").get(0).path("message").path("content").asText();
        if (result.isEmpty()) {
            result = node.path("output").path("text").asText();
        }
        return result.isEmpty() ? "通义千问回复：" + text : result;
    }
    
    @Override
    public String generateResponse(String text) {
        try {
            return callTongyiApi(text);
        } catch (Exception e) {
            log.error("通义千问API调用失败", e);
            throw new RuntimeException("通义千问API调用失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String generateResponse(String text, com.example.airoleplay.entity.Session session) {
        try {
            String prompt = buildRolePlayPrompt(text, session);
            return callTongyiApi(prompt);
        } catch (Exception e) {
            log.error("通义千问API调用失败", e);
            throw new RuntimeException("通义千问API调用失败: " + e.getMessage(), e);
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
    
    private void simulateResponse(String text, String sessionId, WebSocketSession webSocketSession) {
        try {
            String response = "我是通义千问，收到了您的消息：" + text;
            sessionService.saveMessage(sessionId, com.example.airoleplay.entity.Message.Role.assistant, response);
            
            synchronized (webSocketSession) {
                // 发送完整响应
                Map<String, Object> msg = new HashMap<>();
                msg.put("delta", response);
                msg.put("done", true);
                webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            }
        } catch (Exception e) {
            log.error("模拟响应失败", e);
        }
    }
}