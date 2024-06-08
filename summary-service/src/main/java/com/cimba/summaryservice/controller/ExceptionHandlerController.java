package com.cimba.summaryservice.controller;

import com.cimba.summaryservice.model.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<AuthenticationResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        AuthenticationResponse response = AuthenticationResponse.builder().message(ex.getMessage()).build();
        System.out.println("Handling the User NOT found Exception*** " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthenticationResponse> handleBadCredentialsException(BadCredentialsException ex) {
        AuthenticationResponse response = AuthenticationResponse.builder().message(ex.getMessage()).build();
        System.out.println("Handling the Wrong Credentials Exception*** " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
