package com.example.airoleplay.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "audio_mime")
    private String audioMime;

    @Column(name = "audio_duration_ms")
    private Integer audioDurationMs;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();



    public enum Role {
        user, assistant, system
    }
}