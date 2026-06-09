package com.luka.userauth.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenUtil {

    private final boolean IS_PRODUCTION = false;

    private final int COOKIE_MAX_AGE_DAYS = 7 ;

    public void addRefreshToken(HttpServletResponse resp, String token){
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(IS_PRODUCTION);
        cookie.setPath("/auth");
        cookie.setMaxAge(COOKIE_MAX_AGE_DAYS * 24 * 60 * 60);

        resp.addCookie(cookie);
    }

    public String extractFromCookie(HttpServletRequest req){
        if(req.getCookies() == null) return null;

        for(Cookie cookie : req.getCookies()) {
            if("refreshToken".equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

    public void deleteRefreshToken(HttpServletResponse resp){
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(IS_PRODUCTION);
        cookie.setPath("/auth"); //Potencijalni bug
        cookie.setMaxAge(0);

        resp.addCookie(cookie);
    }

}
