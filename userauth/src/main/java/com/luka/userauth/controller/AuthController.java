package com.luka.userauth.controller;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;
import com.luka.userauth.dto.RefreshDto;
import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.security.util.RefreshTokenUtil;
import com.luka.userauth.service.AuthService;
import com.luka.userauth.service.RefreshTokenService;
import com.luka.userauth.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final VerificationService verificationService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenUtil refreshTokenUtil;

    public AuthController(AuthService authService, VerificationService verificationService, RefreshTokenService refreshTokenService, RefreshTokenUtil refreshTokenUtil) {
        this.authService = authService;
        this.verificationService = verificationService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenUtil = refreshTokenUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto request){
        return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
    }

    @GetMapping("/validate-email")
    public ResponseEntity<String> verifyEMail(@RequestParam String token){

        User user = verificationService.verifyUser(token);

        RefreshToken refreshToken = refreshTokenService.create(user);

        return new ResponseEntity<>("Email successfully verified.", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto request, HttpServletResponse resp) {

        LoginResponseDto serviceResp = authService.login(request);

        refreshTokenUtil.addRefreshToken(resp, serviceResp.getRefreshToken());

        return new ResponseEntity<>(serviceResp, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshDto> refresh(HttpServletRequest req, HttpServletResponse resp){

        RefreshToken newToken = refreshTokenService.rotate(refreshTokenUtil.extractFromCookie(req));
        refreshTokenUtil.addRefreshToken(resp, newToken.getToken());

        return new ResponseEntity<>(new RefreshDto("Place for JWT token."), HttpStatus.OK);
    }
}
