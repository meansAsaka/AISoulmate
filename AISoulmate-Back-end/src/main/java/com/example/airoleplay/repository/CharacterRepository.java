package com.example.airoleplay.repository;

import com.example.airoleplay.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharacterRepository extends JpaRepository<Character, String> {
    @Query("SELECT c FROM Character c WHERE " +
           "(:q IS NULL OR c.name LIKE %:q%) AND " +
           "(:locale IS NULL OR c.locale = :locale)")
    List<Character> findByFilters(@Param("q") String q, @Param("locale") String locale);
}