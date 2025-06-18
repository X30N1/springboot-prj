package com.example.demo.services;

import com.example.demo.models.entities.Question;
import com.example.demo.models.entities.Vote;
import com.example.demo.repositories.QuestionRepository;
import com.example.demo.repositories.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, VoteRepository voteRepository) {
        this.questionRepository = questionRepository;
        this.voteRepository = voteRepository;
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
            .orElseThrow(() -> new RuntimeException("Nie znaleziona zapytania z ID: " + id));
    }

    public void delete(Integer id) {
        questionRepository.deleteById(id);
    }

    @Transactional
    public Question vote(Integer id, String ipAddress, boolean voteType) {
        return questionRepository.findById(id)
            .map(question -> {
                Optional<Vote> existingVote = voteRepository.findByQuestionIdAndIpAddress(id, ipAddress);
                
                if (existingVote.isPresent()) {
                    Vote vote = existingVote.get();
                    
                    // Jeśli głos jest taki sam, nie rób nic
                    if (vote.isVoteType() == voteType) {
                        return question;
                    }
                    
                    // Zmień głos - najpierw odejmij stary głos
                    if (vote.isVoteType()) {
                        question.decrementYesVote();
                    } else {
                        question.decrementNoVote();
                    }
                    
                    // Usuń stary głos
                    question.getVotes().remove(vote);
                    voteRepository.delete(vote);
                }
                
                // Dodaj nowy głos
                if (voteType) {
                    question.incrementYesVote();
                } else {
                    question.incrementNoVote();
                }
                
                Vote newVote = new Vote(question, ipAddress, voteType);
                Vote savedVote = voteRepository.save(newVote);
                question.getVotes().add(savedVote);
                
                return questionRepository.save(question);
            })
            .orElseThrow(() -> new RuntimeException("Question not found with ID: " + id));
    }
    
    public boolean hasUserVoted(Integer questionId, String ipAddress) {
        return questionRepository.hasUserVoted(questionId, ipAddress);
    }
    
    public Optional<Boolean> getUserVoteType(Integer questionId, String ipAddress) {
        return questionRepository.getUserVoteType(questionId, ipAddress);
    }
}