package com.cimba.summaryservice.controller;

import com.avik.summaryservice.Summary;
import com.cimba.summaryservice.model.RequestDTO;
import com.cimba.summaryservice.model.SummaryDTO;
import com.cimba.summaryservice.service.SummaryAsyncService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

    private static final Logger logger = LoggerFactory.getLogger(SummaryController.class);
    private final SummaryAsyncService summaryAsyncService;

    @PostMapping("/get-summary")
    public ResponseEntity<SummaryDTO> getSummary(@RequestBody RequestDTO requestDTO) {
        // Get the authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Fetch and save summary asynchronously using the async service
        CompletableFuture<Summary> futureSummary =
                summaryAsyncService.fetchSummaryAsync(requestDTO, username);

        try {
            return futureSummary.thenApply(summary -> {
                // Convert the Summary to a SummaryDTO
                SummaryDTO summaryDTO = SummaryDTO.convertToDTO(summary);
                return ResponseEntity.ok(summaryDTO);
            }).exceptionally(ex -> {
                logger.error("Error fetching summary: " + ex.getMessage(), ex);
                if (ex.getMessage().contains("404")) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching summary. Server Error: ", e);
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
            logger.error("Error fetching summary history. Server Error: ", e);
        throw new RuntimeException("Error fetching summary history. Server Error: ", e);
    }

    }
}