package com.example.airoleplay.controller;

import com.example.airoleplay.entity.Character;
import com.example.airoleplay.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/characters")
@RequiredArgsConstructor
public class CharacterController {
    private final CharacterService characterService;

    @GetMapping
    public ResponseEntity<List<Character>> searchCharacters(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String locale,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "20") Integer limit) {
        List<Character> characters = characterService.searchCharacters(q, locale, tag, limit);
        return ResponseEntity.ok(characters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacter(@PathVariable String id) {
        return characterService.getCharacterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Character> createCharacter(@RequestBody Character character) {
        Character created = characterService.createCharacter(character);
        return ResponseEntity.ok(created);
    }
}