package com.luka.userauth.controller;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;
import com.luka.userauth.entity.User;
import com.luka.userauth.service.AuthService;
import com.luka.userauth.service.VerificationService;
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

    public AuthController(AuthService authService, VerificationService verificationService) {
        this.authService = authService;
        this.verificationService = verificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto request){
        return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
    }

    @GetMapping("/validate-email")
    public ResponseEntity<String> verifyEMail(@RequestParam String token){

        User user = verificationService.verifyUser(token);

        return new ResponseEntity<>("Email successfully verified.", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto request, HttpServletResponse resp) {

        LoginResponseDto serviceResp = authService.login(request);

        return new ResponseEntity<>(serviceResp, HttpStatus.OK);
    }
}
