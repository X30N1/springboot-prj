package com.example.demo.repositories;

import com.example.demo.models.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("SELECT COUNT(v) > 0 FROM Vote v WHERE v.question.id = ?1 AND v.ipAddress = ?2")
    boolean hasUserVoted(Integer questionId, String ipAddress);
}
    