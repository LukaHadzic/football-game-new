package com.luka.userauth.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_token")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token", unique = true, nullable = false)
    private String token;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    @Column(name = "used")
    private boolean used;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public EmailVerificationToken() {}

    public EmailVerificationToken(Long id, String token, LocalDateTime createdAt, LocalDateTime expiresAt, boolean used, User user) {
        this.id = id;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.used = used;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expireAt) {
        this.expiresAt = expireAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
