package com.cimba.summaryservice.service;

import com.avik.summaryservice.Summary;
import com.avik.summaryservice.SummaryService;
import com.cimba.summaryservice.model.RequestDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Service;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SummaryAsyncService {

    private static final Logger logger = LoggerFactory.getLogger(SummaryAsyncService.class);
    private ExecutorService executorService;
    private ExecutorService delegatingExecutorService;
    private ExecutionContextExecutor executionContext;

    @PostConstruct
    public void init() {
        // Initialize the ExecutorService and wrap it with DelegatingSecurityContextExecutorService
        executorService = Executors.newFixedThreadPool(10);
        delegatingExecutorService = new DelegatingSecurityContextExecutorService(executorService);
        executionContext = ExecutionContext.fromExecutor(delegatingExecutorService);
        logger.info("Initialized ExecutorService and ExecutionContext");
    }

    @PreDestroy
    public void destroy() {
        // Shut down the ExecutorService when the bean is destroyed
        executorService.shutdown();
        logger.info("ExecutorService shut down");
    }

    public CompletableFuture<Summary> fetchSummaryAsync(RequestDTO requestDTO, String username) {
        // Use the shared ExecutionContext to fetch and save the summary asynchronously
        CompletableFuture<Summary> futureSummary =
                SummaryService.fetchAndSaveSummary(requestDTO.getUrl(), username, executionContext);
        
        logger.info("Fetching summary asynchronously for user: " + username);
        return futureSummary;
    }

    public CompletableFuture<List<Summary>> fetchSummariesByUsernameAsync(String username) {
        // Use the shared ExecutionContext to fetch summaries by username asynchronously
        CompletableFuture<List<Summary>> futureAllSummaries =
                SummaryService.fetchSummariesByUsername(username, executionContext);
        
        logger.info("Fetching summaries asynchronously for user: " + username);
        return futureAllSummaries;
    }

}
