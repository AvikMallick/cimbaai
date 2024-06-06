package com.cimba.summaryservice.controller;

import com.avik.summaryservice.SummaryService;

import com.cimba.summaryservice.model.RequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        // Create an ExecutionContext from the ExecutorService
        ExecutionContextExecutor executionContext = ExecutionContext.fromExecutor(executorService);

        // Pass the ExecutionContext as the second argument to the fetchAndSaveSummary method
        CompletableFuture<String> futureSummary =
                SummaryService.fetchAndSaveSummary(requestDTO.getUrl(), executionContext);

        try {
            // Fetch and save summary asynchronously
            return futureSummary.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching summary", e);
            throw new RuntimeException("Error fetching summary", e);
        } finally {
            // Ensure resources are closed
            SummaryService.shutdown();
        }
    }
}


//@PostMapping("/summary")
//    public CompletableFuture<String> getUrlSummary(@RequestBody RequestDTO requestDTO) {
//        System.out.println("Received request to fetch summary for: " + requestDTO.getUrl());
//        return urlSummaryService.getSummary(requestDTO.getUrl())
//                .exceptionally(ex -> {
//                    // Handle exceptions
//                    return "Failed to fetch summary: " + ex.getMessage();
//                });
//    }