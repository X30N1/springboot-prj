package com.example.demo.models.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "questions")
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

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();

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

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    public Question() {
        this.yesvote = 0;
        this.novote = 0;
        this.votes = new HashSet<>();
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

    public void incrementYesVote() {
        this.yesvote++;
    }

    public void incrementNoVote() {
        this.novote++;
    }
}
