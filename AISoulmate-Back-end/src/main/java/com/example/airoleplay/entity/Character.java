package com.example.airoleplay.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "characters")
public class Character {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(nullable = false)
    private String name;

    private String locale = "zh-CN";

    @Column(columnDefinition = "json")
    private String tags;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String brief;

    private Integer popularity = 0;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


}