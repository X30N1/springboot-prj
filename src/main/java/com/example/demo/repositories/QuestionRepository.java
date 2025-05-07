package com.example.demo.repositories;

import com.example.demo.models.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionRepository extends JpaRepository<Question, Integer> {
}
