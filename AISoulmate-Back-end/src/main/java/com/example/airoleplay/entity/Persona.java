package com.example.airoleplay.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "personas")
public class Persona {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "character_id", nullable = false)
    private String characterId;

    @Column(name = "persona_yaml", nullable = false, columnDefinition = "TEXT")
    private String personaYaml;

    @Column(name = "persona_json", columnDefinition = "json")
    private String personaJson;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();


}