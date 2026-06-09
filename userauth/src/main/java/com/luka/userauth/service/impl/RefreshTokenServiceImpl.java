package com.luka.userauth.service.impl;

import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.RefreshTokenException;
import com.luka.userauth.repository.RefreshTokenRepository;
import com.luka.userauth.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final long REFRESH_TOKEN_VALID_FOR_DAYS = 7;

    private final long TOKEN_VALID_LENGTH = 36;

    private final RefreshTokenRepository refreshTokenRepository;

    private final TransactionTemplate transactionTemplate;

    private final Clock clock;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, TransactionTemplate transactionTemplate, Clock clock) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.transactionTemplate = transactionTemplate;
        this.clock = clock;
    }

    @Override
    public RefreshToken create(User user) {

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(user);
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setRevoked(false);
        newToken.setCreatedAt(LocalDateTime.now(clock));
        newToken.setExpiresAt(LocalDateTime.now(clock).plusDays(REFRESH_TOKEN_VALID_FOR_DAYS));

        return refreshTokenRepository.save(newToken);
    }

    @Override
    public RefreshToken validate(String token) {

        if (token == null || token.length() != TOKEN_VALID_LENGTH) return null;

        RefreshToken dbToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Invalid refresh token."));

        if (dbToken.isRevoked() || dbToken.isExpired(LocalDateTime.now(clock))) return null;

        return dbToken;

    }

    @Override
    public RefreshToken validateOnLogin(User user){
        Optional<RefreshToken> dbToken = refreshTokenRepository.findByUserAndRevokedFalse(user);

        if (dbToken.isPresent()){
            RefreshToken refreshToken = dbToken.get();
            if (refreshToken.isExpired(LocalDateTime.now(clock))){
                revoke(refreshToken.getToken());
                return create(user);
            }
            return refreshToken;
        }

        return create(user);

    }

    @Override
    public RefreshToken rotate(String token) {

        RefreshToken newToken = new RefreshToken();
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setRevoked(false);
        newToken.setCreatedAt(LocalDateTime.now(clock));
        newToken.setExpiresAt(LocalDateTime.now(clock).plusDays(REFRESH_TOKEN_VALID_FOR_DAYS));

        try {
            return transactionTemplate.execute(status -> {
//                RefreshToken oldDbToken = refreshTokenRepository.findByToken(token)
//                        .orElseThrow(() -> new RefreshTokenException("Refresh token not valid."));
//
//                if(oldDbToken.isRevoked()){
//                    throw new RefreshTokenException("Refresh token already revoked.");
//                }
//
//                if(oldDbToken.isExpired()){
//                    throw new RefreshTokenException("Refresh token expired.");
//                }

                RefreshToken oldDbToken = validate(token);

                if (oldDbToken == null) throw new RefreshTokenException("Token not valid.");

                newToken.setUser(oldDbToken.getUser());
                revoke(token);

                refreshTokenRepository.save(oldDbToken);
                return refreshTokenRepository.save(newToken);

            });
        }catch (RefreshTokenException re) {
            throw re;

        }catch(Exception e) {
            e.printStackTrace();
            throw new RefreshTokenException("Server error - refreshing failed, please try again later.");
        }
    }

    @Override
    public void revoke(String token) {

        if (token == null || token.length() != TOKEN_VALID_LENGTH) {
            throw new RefreshTokenException("Refresh token not valid.");
        }

        transactionTemplate.executeWithoutResult(status -> {
            int updated = refreshTokenRepository.revokeByToken(token);

            if (updated == 0) {
                throw new RefreshTokenException("Refresh token not valid.");
            }
        });
    }

    @Override
    public long getREFRESH_TOKEN_VALID_FOR_DAYS() {
        return REFRESH_TOKEN_VALID_FOR_DAYS;
    }

    @Override
    public long getTOKEN_VALID_LENGTH() {
        return TOKEN_VALID_LENGTH;
    }

}
