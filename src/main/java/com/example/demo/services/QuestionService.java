package com.example.demo.services;

import com.example.demo.models.entities.Question;
import com.example.demo.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Optional<Question> findById(Integer id) {
        return questionRepository.findById(id);
    }

    public Question edit(Integer id, Question updatedQuestion) {
        return questionRepository.findById(id)
            .map(question -> {
                return questionRepository.save(updatedQuestion);
            })
            .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
    }

    public void delete(Integer id) {
        questionRepository.deleteById(id);
    }
}
