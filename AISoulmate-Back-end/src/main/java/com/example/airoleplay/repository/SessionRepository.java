package com.example.airoleplay.repository;

import com.example.airoleplay.dto.SessionHistoryDto;
import com.example.airoleplay.entity.Session;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, String> {
    // src/main/java/com/example/airoleplay/repository/SessionRepository.java
    @Query("""
    SELECT new com.example.airoleplay.dto.SessionHistoryDto(
        s.id,
        c.avatarUrl,
        (SELECT m.text FROM Message m WHERE m.sessionId = s.id ORDER BY m.createdAt DESC LIMIT 1)
    )
    FROM Session s
    JOIN Character c ON s.characterId = c.id
    WHERE s.userId = :userId
""")
    List<SessionHistoryDto> findSessionHistoriesByUserId(@Param("userId") String userId);

}