package com.cimba.summaryservice.controller;

import com.cimba.summaryservice.model.TokenValidationResponseDTO;
import com.cimba.summaryservice.service.JwtService;
import com.cimba.summaryservice.service.UserDetailsServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtService jwtService;
    private final UserDetailsServiceImp userDetailsService;

    @GetMapping("/validate_token")
    public ResponseEntity<TokenValidationResponseDTO> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            TokenValidationResponseDTO response = TokenValidationResponseDTO.builder()
                    .valid(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isValid(token, userDetails)) {
            TokenValidationResponseDTO response = TokenValidationResponseDTO.builder()
                    .valid(true)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            TokenValidationResponseDTO response = TokenValidationResponseDTO.builder()
                    .valid(false)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
