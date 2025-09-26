package com.example.airoleplay.repository;

import com.example.airoleplay.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, String> {
    List<Session> findByUserId(String userId);
}