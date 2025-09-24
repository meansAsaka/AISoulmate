package com.example.airoleplay.service.impl;

import com.example.airoleplay.dto.CreateSessionRequest;
import com.example.airoleplay.entity.Message;
import com.example.airoleplay.entity.Session;
import com.example.airoleplay.repository.MessageRepository;
import com.example.airoleplay.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    public Session createSession(CreateSessionRequest request) {
        Session session = new Session();
        session.setUserId("test-user-1"); // 简化处理，使用固定用户ID
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
}