package com.luka.userauth.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token", unique = true, nullable = false)
    private String token;
    @Column(name = "revoked", nullable = false)
    private boolean revoked;
    @Column (name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column (name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public RefreshToken() {
    }

    public RefreshToken(Long id, String token, boolean revoked, LocalDateTime createdAt, LocalDateTime expiresAt, User user) {
        this.id = id;
        this.token = token;
        this.revoked = revoked;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
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

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean used) {
        this.revoked = used;
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

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isExpired(LocalDateTime now){
        return this.expiresAt.isBefore(now);
    }
}
