package com.example.airoleplay.dto;

import com.example.airoleplay.entity.Session;
import lombok.Data;

@Data
public class CreateSessionRequest {
    private String characterId;
    private Session.ChatMode mode = Session.ChatMode.immersive;
}