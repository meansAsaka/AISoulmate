package com.example.airoleplay.repository;

import com.example.airoleplay.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {
}