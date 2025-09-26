package com.example.airoleplay.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "character_id", nullable = false)
    private String characterId;

    @Enumerated(EnumType.STRING)
    private ChatMode mode = ChatMode.immersive;

    private String summary;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();



    public enum ChatMode {
        immersive, academic, socratic
    }
}