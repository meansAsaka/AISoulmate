package com.example.airoleplay.service.impl;

import com.example.airoleplay.service.LlmService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class TongyiLlmService implements LlmService {
    private final SessionService sessionService;
    private final CharacterService characterService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatClient chatClient;

    public TongyiLlmService(
        SessionService sessionService,
        CharacterService characterService,
        @Qualifier("tongyiChatClient") ChatClient chatClient
    ) {
        this.sessionService = sessionService;
        this.characterService = characterService;
        this.chatClient = chatClient;
    }

    @Override
    public void streamChat(String text, String sessionId, WebSocketSession webSocketSession) {
        try {
            sessionService.saveMessage(sessionId, com.example.airoleplay.entity.Message.Role.user, text);
            sessionService.saveMessagesToRedis(sessionId,  text);

            com.example.airoleplay.entity.Session session = sessionService.getSessionById(sessionId).orElse(null);
            if (session == null) {
                log.error("未找到会话: {}", sessionId);
                return;
            }

            List<String> historytexts = sessionService.getMessagesFromRedis(session.getId());
            String prompt = PromptBuilder.buildRolePlayPrompt(text, session, characterService, historytexts);

            String response = callTongyiAi(prompt);
            sessionService.saveMessage(sessionId, com.example.airoleplay.entity.Message.Role.assistant, response);
            sessionService.saveMessagesToRedis(sessionId, response);

            synchronized (webSocketSession) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("reply", response);
                msg.put("done", true);
                webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            }

        } catch (Exception e) {
            log.error("通义千问AI调用失败", e);
        }
    }

    private String callTongyiAi(String prompt) {
        Prompt springAiPrompt = new Prompt(List.of(new UserMessage(prompt)));
        Generation generation = chatClient.call(springAiPrompt).getResult();
        String result = generation.getOutput().getContent();
        Map<String, Object> msg = new HashMap<>();
        msg.put("reply", result);
        msg.put("done", true);
        try {
            return objectMapper.writeValueAsString(msg);
        } catch (Exception e) {
            return result;
        }
    }

    @Override
    public String generateResponse(String text) {
        return callTongyiAi(text);
    }

    @Override
    public String generateResponse(String text, com.example.airoleplay.entity.Session session) {
        List<String> historytexts = sessionService.getMessagesFromRedis(session.getId());
        String prompt = PromptBuilder.buildRolePlayPrompt(text, session, characterService, historytexts);
        return callTongyiAi(prompt);
    }
}