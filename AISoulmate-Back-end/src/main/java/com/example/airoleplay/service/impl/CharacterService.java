package com.example.airoleplay.service.impl;

import com.example.airoleplay.entity.Character;
import com.example.airoleplay.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CharacterService {
    private final CharacterRepository characterRepository;

    public List<Character> searchCharacters(String q, String locale, String tag, Integer limit) {
        List<Character> characters = characterRepository.findByFilters(q, locale);
        return characters.stream().limit(limit != null ? limit : 20).toList();
    }

    public Optional<Character> getCharacterById(String id) {
        return characterRepository.findById(id);
    }

    public Character createCharacter(Character character) {
        return characterRepository.save(character);
    }
}