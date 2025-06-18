package com.example.demo.controllers;

import com.example.demo.models.dtos.QuestionDto;
import com.example.demo.models.entities.Question;
import com.example.demo.services.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
    
    @GetMapping("/{id}/vote-status")
    public ResponseEntity<Object> getVoteStatus(@PathVariable Integer id, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        boolean hasVoted = questionService.hasUserVoted(id, ipAddress);
        
        if (!hasVoted) {
            return ResponseEntity.ok(new VoteStatusResponse(false, null));
        }
        
        Optional<Boolean> voteType = questionService.getUserVoteType(id, ipAddress);
        return ResponseEntity.ok(new VoteStatusResponse(true, voteType.orElse(null)));
    }
    
    // Klasa pomocnicza dla odpowiedzi statusu głosu
    public static class VoteStatusResponse {
        private boolean hasVoted;
        private Boolean voteType; // true = yes, false = no, null = no vote
        
        public VoteStatusResponse(boolean hasVoted, Boolean voteType) {
            this.hasVoted = hasVoted;
            this.voteType = voteType;
        }
        
        public boolean isHasVoted() {
            return hasVoted;
        }
        
        public Boolean getVoteType() {
            return voteType;
        }
    }
}