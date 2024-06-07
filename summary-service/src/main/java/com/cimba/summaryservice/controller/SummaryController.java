package com.cimba.summaryservice.controller;

import com.avik.summaryservice.SummaryService;

import com.cimba.summaryservice.model.RequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SummaryController {

    @PostMapping("/get-summary")
    public ResponseEntity<String> getSummary(@RequestBody RequestDTO requestDTO) {
        // Get the authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Create an ExecutorService with a fixed thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Wrap the ExecutorService in a DelegatingSecurityContextExecutorService
        ExecutorService delegatingExecutorService = new DelegatingSecurityContextExecutorService(executorService);

        // Create an ExecutionContext from the ExecutorService
        ExecutionContextExecutor executionContext = ExecutionContext.fromExecutor(delegatingExecutorService);

        // Pass the ExecutionContext as the second argument to the fetchAndSaveSummary method
        CompletableFuture<String> futureSummary =
                SummaryService.fetchAndSaveSummary(requestDTO.getUrl(), username, executionContext);

        try {
            // Fetch and save summary asynchronously
            return futureSummary.thenApply(ResponseEntity::ok).exceptionally(ex -> {
                System.out.println("****ERROR***");
                System.out.println(ex.getMessage());
            if(ex.getMessage().contains("404")) {
                return new ResponseEntity<>("The given URL is not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Error fetching summary: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }).get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("****ERROR 2***");
            System.out.println(e.getMessage());
            throw new RuntimeException("Error fetching summary. Server Error: ", e);
        }
    }
}