package com.example.airoleplay.repository;

import com.example.airoleplay.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findBySessionIdOrderByCreatedAtAsc(String sessionId);
}