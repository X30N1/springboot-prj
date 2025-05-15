package com.example.demo.controllers;

import com.example.demo.models.dtos.QuestionDto;
import com.example.demo.models.entities.Question;
import com.example.demo.services.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<QuestionDto> createQuestion(@RequestBody Question question) {
        Question saved = questionService.save(question);
        return ResponseEntity.ok(QuestionDto.fromEntity(saved));
    }

    @GetMapping
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        List<QuestionDto> questions = questionService.findAll()
            .stream()
            .map(QuestionDto::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestion(@PathVariable Integer id) {
        return questionService.findById(id)
            .map(question -> ResponseEntity.ok(QuestionDto.fromEntity(question)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDto> updateQuestion(@PathVariable Integer id, @RequestBody Question question) {
        Question updated = questionService.edit(id, question);
        return ResponseEntity.ok(QuestionDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/vote/yes")
    public ResponseEntity<QuestionDto> voteYes(@PathVariable Integer id, HttpServletRequest request) {
        Question voted = questionService.vote(id, request.getRemoteAddr(), true);
        return ResponseEntity.ok(QuestionDto.fromEntity(voted));
    }

    @PostMapping("/{id}/vote/no")
    public ResponseEntity<QuestionDto> voteNo(@PathVariable Integer id, HttpServletRequest request) {
        Question voted = questionService.vote(id, request.getRemoteAddr(), false);
        return ResponseEntity.ok(QuestionDto.fromEntity(voted));
    }
}