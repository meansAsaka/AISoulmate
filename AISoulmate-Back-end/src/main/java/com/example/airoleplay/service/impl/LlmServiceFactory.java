package com.example.airoleplay.service.impl;

import com.example.airoleplay.service.LlmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LlmServiceFactory {
    private final TongyiLlmService tongyiLlmService;
    private final DeepSeekLlmService deepSeekLlmService;

    public LlmService getService(String modelName) {
        return switch (modelName.toLowerCase()) {
            case "deepseek" -> deepSeekLlmService;
            case "tongyi" -> tongyiLlmService;
            default -> tongyiLlmService;
        };
    }
}