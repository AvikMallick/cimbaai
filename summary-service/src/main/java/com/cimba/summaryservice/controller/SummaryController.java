package com.cimba.summaryservice.controller;

import com.avik.summaryservice.SummaryService;

import com.cimba.summaryservice.model.RequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.web.bind.annotation.*;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SummaryController {
    private static final Logger logger = LoggerFactory.getLogger(SummaryController.class);

    @PostMapping("/get-summary")
    public String getSummary(@RequestBody RequestDTO requestDTO) {
        // Create an ExecutorService with a fixed thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Wrap the ExecutorService in a DelegatingSecurityContextExecutorService
        ExecutorService delegatingExecutorService = new DelegatingSecurityContextExecutorService(executorService);

        // Create an ExecutionContext from the ExecutorService
        ExecutionContextExecutor executionContext = ExecutionContext.fromExecutor(delegatingExecutorService);

        // Pass the ExecutionContext as the second argument to the fetchAndSaveSummary method
        CompletableFuture<String> futureSummary =
                SummaryService.fetchAndSaveSummary(requestDTO.getUrl(), executionContext);

        try {
            // Fetch and save summary asynchronously
            return futureSummary.exceptionally(ex -> {
                logger.error("Error fetching summary handled", ex);
                return "Error fetching summary. The ex ->" + ex;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching summary", e);
            throw new RuntimeException("Error fetching summary", e);
        }
    }
}