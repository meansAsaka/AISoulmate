package com.example.airoleplay.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "citations")
public class Citation {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(nullable = false)
    private String source;

    private String url;

    @Column(columnDefinition = "TEXT")
    private String snippet;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;


}