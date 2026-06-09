package com.luka.userauth.security.util;

import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class responsible for JWT token. It is utility class that can generate, validate or extract data from
 * JWT token.
 *
 * @Author Luka
 * @Version 1.0
 * @since 17. May 2026.
 */
@Component
public class JWTUtil {

    private final String JWT_SECRET = "mySecretJwT12987rw890atb456ksk01";
    private final long VALID_FOR_MILISECONDS = 1000*60*60;
    private final String ISSUER_NAME = "user-auth-service";
    private SecretKey key;

//    private final Clock clock;
//
//    public JWTUtil(Clock clock) {
//        this.clock = clock;
//    }

    /**
     * When instance of JWTUtil class is created, this method sets proper secret key necessary for cyphering.
     */
    @PostConstruct
    public void onInit() {
        this.key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }
    //Generate and return JWT token

    /**
     * Generates JWT token with following attributes: user's id, issuer's name, user's roles, date of creation and
     * date of expiration.
     * Generated token is valid for one hour.
     *
     * @param user
     * @return String that represents generated JWT token.
     */
    public String generateToken(User user){
        //Extract role names from user
        Set<String> userRolesSet = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        System.out.println("Veličina seta: " + userRolesSet.size());
        userRolesSet.stream().forEach(System.out::println);

        return Jwts.builder()
                .claim("roles", userRolesSet)
                .subject(String.valueOf(user.getId()))
                .issuer(ISSUER_NAME)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + VALID_FOR_MILISECONDS))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts user's id from provided JWT token.
     *
     * @param token
     * @return Long that represents user's id contained in JWT token.
     */
    public Long extractUserId(String token){
        String userId = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.parseLong(userId);
    }

    /**
     * Extracts expiration date from provided JWT token.
     *
     * @param token
     * @return Date that represents expiration date of contained in JWT token.
     */
    public Date extractExpirationDate(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    /**
     * Validates provided JWT token if it's signed with correct key and also if it's not expired.
     *
     * @param token
     * @return Boolean that represents if provided JWT token is valid or not.
     */
    public boolean isTokenValid(String token){
        try {
            if (extractExpirationDate(token).before(new Date())) throw new JwtException("Token expired.");
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    /**
     * Extracts user's roles from provided JWT token.
     *
     * @param token
     * @return Set that contains user's roles contained in JWT token.
     */
    public Set<String> extractRoles(String token){
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        List<String> roles = claims.get("roles", List.class);
        return new HashSet<>(roles);
    }

}
