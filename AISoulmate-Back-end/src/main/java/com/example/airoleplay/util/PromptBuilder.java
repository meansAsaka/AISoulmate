package com.example.airoleplay.util;

import com.example.airoleplay.entity.Message;
import com.example.airoleplay.entity.Session;
import com.example.airoleplay.service.impl.CharacterService;

import java.util.List;

public class PromptBuilder {
    public static String buildRolePlayPrompt(String userText, Session session, CharacterService characterService, List<String> historytexts) {
        StringBuilder prompt = new StringBuilder();

        characterService.getCharacterById(session.getCharacterId())
                .ifPresent(character -> {
                    prompt.append("你现在要扮演角色：").append(character.getName()).append("\n");
                    if (character.getBrief() != null) {
                        prompt.append("角色描述：").append(character.getBrief()).append("\n");
                    }
                });

        switch (session.getMode()) {
            case immersive:
                prompt.append("请完全沉浸在这个角色中，用第一人称回答，保持角色的性格特点和说话方式。\n");
                break;
            case academic:
                prompt.append("请以学术讨论的方式，结合角色的知识背景来回答问题。\n");
                break;
            case socratic:
                prompt.append("请用苏格拉底式的问答方法，通过提问来引导思考。\n");
                break;
        }

        StringBuilder history = new StringBuilder();
        history.append("会话ID: ").append(session.getId()).append("\n");
        for (String text : historytexts) {
            history.append(text).append("\n");
        }
        history.append("\n");
        prompt.append("\n你们的历史对话：\n").append(history);

        prompt.append("\n用户问题：").append(userText);

        return prompt.toString();
    }
}
