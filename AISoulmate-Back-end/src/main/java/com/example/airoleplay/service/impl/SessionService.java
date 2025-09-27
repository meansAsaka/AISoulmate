package com.example.airoleplay.service.impl;

import ch.qos.logback.classic.Logger;
import com.example.airoleplay.dto.CreateSessionRequest;
import com.example.airoleplay.dto.SessionHistoryDto;
import com.example.airoleplay.entity.Message;
import com.example.airoleplay.entity.Session;
import com.example.airoleplay.repository.MessageRepository;
import com.example.airoleplay.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SessionService.class);

    public Session createSession(String userId, CreateSessionRequest request) {
        Session session = new Session();
        session.setUserId(userId); // 使用实际用户ID
        session.setCharacterId(request.getCharacterId());
        session.setMode(request.getMode());

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
        try {
            String counterKey = "session:messages:counter:" + sessionId;
            Long messageIndex = redisTemplate.opsForValue().increment(counterKey);
            String hashKey = "session:messages:" + sessionId + ":" + messageIndex;
            redisTemplate.opsForHash().put(hashKey, messageIndex.toString(), text);
            // 同时设置计数器和哈希表的过期时间
            redisTemplate.expire(counterKey, 5, TimeUnit.MINUTES);
            redisTemplate.expire(hashKey, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis存储消息失败，sessionId={}, text={}, 原因: {}", sessionId, text, e.getMessage());
        }
    }

    // 根据 sessionId 从 Redis 获取历史消息
    public List<String> getMessagesFromRedis(String sessionId) {
        try {
            String counterKey = "session:messages:counter:" + sessionId;
            String maxIndexStr = redisTemplate.opsForValue().get(counterKey);
            Long maxIndex = (maxIndexStr == null) ? 0L : Long.parseLong(maxIndexStr);
            if (maxIndex == 0) {
                return List.of();
            }
            List<String> messages = new ArrayList<>();
            for (long i = 1; i < maxIndex; i++) {
                String hashKey = "session:messages:" + sessionId + ":" + i;
                Object value = redisTemplate.opsForHash().get(hashKey, String.valueOf(i));
                if (value != null) {
                    messages.add(value.toString());
                }
            }
            return messages;
        } catch (Exception e) {
            log.warn("Redis读取消息失败，sessionId={}, 原因: {}", sessionId, e.getMessage());
            return List.of();
        }
    }

    public List<SessionHistoryDto> getSessionHistoriesByUserId(String userId) {
        return sessionRepository.findSessionHistoriesByUserId(userId);
    }
}