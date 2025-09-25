package com.example.airoleplay.repository;

import com.example.airoleplay.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, String> {
    // 查询当前会话最新的五个消息

}