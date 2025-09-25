package com.example.airoleplay.service.impl;

import com.example.airoleplay.dto.CreateSessionRequest;
import com.example.airoleplay.entity.Message;
import com.example.airoleplay.entity.Session;
import com.example.airoleplay.repository.MessageRepository;
import com.example.airoleplay.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public Session createSession(String userId, CreateSessionRequest request) {
        Session session = new Session();
        session.setUserId(userId); // 使用实际用户ID
        session.setCharacterId(request.getCharacterId());
        session.setMode(request.getMode());
        session.setModelName(request.getModelName());
        return sessionRepository.save(session);
    }

    public Optional<Session> getSessionById(String id) {
        return sessionRepository.findById(id);
    }

    public List<Message> getSessionMessages(String sessionId) {
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    public Message saveMessage(String sessionId, Message.Role role, String text) {
        Message message = new Message();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setText(text);
        return messageRepository.save(message);
    }

    public List<Message> getLatestSessionMessagesByUserId(String sessionId) {
        return messageRepository.findTop10BySessionIdOrderByCreatedAtDesc(sessionId);
    }

    /**
     * 将历史消息存入Redis，过期时间5分钟
     */
    public void saveMessagesToRedis(String sessionId, String text) {
        String redisKey = "session:messages:" + sessionId;
        redisTemplate.opsForList().rightPush(redisKey, text);
        redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES); // 设置过期时间
    }

    /**
     * 根据 sessionId 从 Redis 获取历史消息
     */
    public List<String> getMessagesFromRedis(String sessionId) {
        String redisKey = "session:messages:" + sessionId;
        List<String> list = redisTemplate.opsForList().range(redisKey, 0, -1);
        return list != null ? list : List.of();
    }
}