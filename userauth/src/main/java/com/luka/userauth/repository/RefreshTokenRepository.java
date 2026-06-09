package com.luka.userauth.repository;

import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    public Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.token = :token")
    int revokeByToken(String token);

    Optional<RefreshToken> findByUserAndRevokedFalse(User user);
}
