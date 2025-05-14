package com.example.demo.models.dtos;

import com.example.demo.models.entities.Question;

import java.time.LocalDateTime;

public class QuestionDto {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createtime;
    private LocalDateTime endtime;
    private Integer yesvote;
    private Integer novote;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public LocalDateTime getEndtime() {
        return endtime;
    }

    public Integer getYesvote() {
        return yesvote;
    }

    public Integer getNovote() {
        return novote;
    }

    public QuestionDto(Integer id, String name, String description, LocalDateTime createtime, LocalDateTime endtime, Integer yesvote, Integer novote) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createtime = createtime;
        this.endtime = endtime;
        this.yesvote = yesvote;
        this.novote = novote;
    }

    public static QuestionDto fromEntity(Question question){
        return new QuestionDto(
                question.getId(),
                question.getName(),
                question.getDescription(),
                question.getCreatetime(),
                question.getEndtime(),
                question.getYesvote(),
                question.getNovote()
        );
    }
}


