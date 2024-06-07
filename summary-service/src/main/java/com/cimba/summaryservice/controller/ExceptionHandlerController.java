package com.cimba.summaryservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<String> handleException() {
        return ResponseEntity.ok("Exception occurred");
    }
}
