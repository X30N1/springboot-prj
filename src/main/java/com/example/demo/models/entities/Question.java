package com.example.demo.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Question {

    @Id
    @GeneratedValue
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

    public Question(){

    }

    public Question(Integer id, String name, String description, LocalDateTime endtime, Integer yesvote, Integer novote) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createtime = LocalDateTime.now();
        this.endtime = endtime;
        this.yesvote = yesvote;
        this.novote = novote;
    }
}
