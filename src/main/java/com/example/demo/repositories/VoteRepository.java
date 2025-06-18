package com.example.demo.repositories;

import com.example.demo.models.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {
    
    @Query("SELECT v FROM Vote v WHERE v.question.id = ?1 AND v.ipAddress = ?2")
    Optional<Vote> findByQuestionIdAndIpAddress(Integer questionId, String ipAddress);
    
    @Query("DELETE FROM Vote v WHERE v.question.id = ?1 AND v.ipAddress = ?2")
    void deleteByQuestionIdAndIpAddress(Integer questionId, String ipAddress);
}