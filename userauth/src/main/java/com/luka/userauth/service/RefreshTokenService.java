package com.luka.userauth.service;

import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.User;

public interface RefreshTokenService {

    RefreshToken create(User user);

    RefreshToken validate(String token);

    RefreshToken validateOnLogin(User user);

    RefreshToken rotate(String token);

    void revoke(String token);

    long getREFRESH_TOKEN_VALID_FOR_DAYS();

    long getTOKEN_VALID_LENGTH();
}
