package com.luka.userauth.security.filters;

import com.luka.userauth.exception.exceptionclasses.JWTInvalidException;
import com.luka.userauth.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req){
        String path = req.getServletPath();
        return path.equals("/auth/register")
                || path.equals("/auth/login")
                || path.equals("/auth/validate-email")
                || path.equals("/auth/logout")
                || path.equals("/auth/refresh");
    }

    private String getToken(String reqAuthHeader){
        if (reqAuthHeader != null && reqAuthHeader.startsWith("Bearer ")){
            return reqAuthHeader.substring(7);
        }else{
            throw new JWTInvalidException("JWT token is not valid.");
        }
    }

    private List<GrantedAuthority> getAuthorities(String token){
        Set<String> roles = jwtUtil.extractRoles(token);

        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request.getHeader("Authorization"));

        if (jwtUtil.isTokenValid(token)){
            if (SecurityContextHolder.getContext().getAuthentication() == null){
                Long userId = jwtUtil.extractUserId(token);
                List<GrantedAuthority> authorities = getAuthorities(token);
                UsernamePasswordAuthenticationToken loggedUser =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(loggedUser);
            }
        }else{
            throw new JWTInvalidException("JWT token is not valid.");
        }
        filterChain.doFilter(request, response);
    }

}
