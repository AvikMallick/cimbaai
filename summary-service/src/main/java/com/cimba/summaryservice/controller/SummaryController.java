package com.cimba.summaryservice.controller;

import com.avik.summaryservice.Summary;
import com.avik.summaryservice.SummaryService;

import com.cimba.summaryservice.model.RequestDTO;
import com.cimba.summaryservice.model.SummaryDTO;
import com.cimba.summaryservice.service.SummaryAsyncService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

//@RestController
//public class SummaryController {
//
//    @PostConstruct
//    public void init() {
//        // Set the SecurityContextHolder strategy to MODE_INHERITABLETHREADLOCAL
//        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
//    }
//
//    @PostMapping("/get-summary")
//    public ResponseEntity<String> getSummary(@RequestBody RequestDTO requestDTO) {
//        // Get the authenticated user's username
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        // Create an ExecutorService with a fixed thread pool
//        ExecutorService executorService = Executors.newFixedThreadPool(10);
//
//        // Wrap the ExecutorService in a DelegatingSecurityContextExecutorService
//        ExecutorService delegatingExecutorService = new DelegatingSecurityContextExecutorService(executorService);
//
//        // Create an ExecutionContext from the ExecutorService
//        ExecutionContextExecutor executionContext = ExecutionContext.fromExecutor(delegatingExecutorService);
//
//        // Pass the ExecutionContext as the second argument to the fetchAndSaveSummary method
//        CompletableFuture<String> futureSummary =
//                SummaryService.fetchAndSaveSummary(requestDTO.getUrl(), username, executionContext);
//
//        try {
//            // Fetch and save summary asynchronously
//            return futureSummary.thenApply(ResponseEntity::ok).exceptionally(ex -> {
//                System.out.println("****ERROR***");
//                System.out.println(ex.getMessage());
//            if(ex.getMessage().contains("404")) {
//                return new ResponseEntity<>("The given URL is not found", HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>("Error fetching summary: " + ex.getMessage(),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        }).get();
//        } catch (InterruptedException | ExecutionException e) {
//            System.out.println("****ERROR 2***");
//            System.out.println(e.getMessage());
//            throw new RuntimeException("Error fetching summary. Server Error: ", e);
//        } finally {
//            // Shut down the ExecutorService
//            executorService.shutdown();
//        }
//    }
//}

@RestController
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryAsyncService summaryAsyncService;

    @PostMapping("/get-summary")
    public ResponseEntity<String> getSummary(@RequestBody RequestDTO requestDTO) {
        // Get the authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Fetch and save summary asynchronously using the async service
        CompletableFuture<String> futureSummary = summaryAsyncService.fetchSummaryAsync(requestDTO, username);

        try {
            return futureSummary.thenApply(ResponseEntity::ok).exceptionally(ex -> {
                if (ex.getMessage().contains("404")) {
                    return new ResponseEntity<>("The given URL is not found", HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>("Error fetching summary: " + ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching summary. Server Error: ", e);
        }
    }

    @GetMapping("/get-summary-history")
    public ResponseEntity<List<SummaryDTO>> getSummaryHistory() {
        // Get the authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Fetch summaries by username asynchronously using the async service
        CompletableFuture<List<Summary>> futureAllSummaries = summaryAsyncService.fetchSummariesByUsernameAsync(username);

        try {
        List<SummaryDTO> summaryDTOList = futureAllSummaries.get().stream()
                .map(SummaryDTO::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(summaryDTOList);
    } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException("Error fetching summary history. Server Error: ", e);
    }

//        try {
//            ResponseEntity<List<Summary>> listResponseEntity =
//                    futureAllSummaries.thenApply(ResponseEntity::ok).exceptionally(ex -> {
//                        List<SummaryDTO> emptySummaries = new ArrayList<SummaryDTO>();
//                        return new ResponseEntity<>(emptySummaries, HttpStatus.INTERNAL_SERVER_ERROR);
//                    }).get();
//
//            List<SummaryDTO> summaries =
//            System.out.println("Returning the summary history" + listResponseEntity.getBody());
//            return listResponseEntity;
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException("Error fetching summary history. Server Error: ", e);
//        }
    }
}