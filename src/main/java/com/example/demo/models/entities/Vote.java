package com.example.demo.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    
    @Column(name = "vote_type", nullable = false)
    private boolean voteType;

    public Vote() {}

    public Vote(Question question, String ipAddress, boolean voteType) {
        this.question = question;
        this.ipAddress = ipAddress;
        this.voteType = voteType;
    }

    // Getters
    public Integer getId() { return id; }
    public Question getQuestion() { return question; }
    public String getIpAddress() { return ipAddress; }
    public boolean isVoteType() { return voteType; }
}
