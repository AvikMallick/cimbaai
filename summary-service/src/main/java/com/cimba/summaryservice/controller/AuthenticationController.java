package com.cimba.summaryservice.controller;

import com.cimba.summaryservice.config.CustomLogoutHandler;
import com.cimba.summaryservice.model.AuthenticationResponse;
import com.cimba.summaryservice.model.User;
import com.cimba.summaryservice.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    private final AuthenticationService authService;
    private final CustomLogoutHandler customLogoutHandler;

    public AuthenticationController(AuthenticationService authService, CustomLogoutHandler customLogoutHandler) {
        this.authService = authService;
        this.customLogoutHandler = customLogoutHandler;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return authService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        customLogoutHandler.logout(request, response, auth);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
