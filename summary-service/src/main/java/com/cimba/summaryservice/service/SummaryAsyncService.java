package com.cimba.summaryservice.service;

import com.avik.summaryservice.Summary;
import com.avik.summaryservice.SummaryService;
import com.cimba.summaryservice.model.RequestDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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

    private ExecutorService executorService;
    private ExecutorService delegatingExecutorService;
    private ExecutionContextExecutor executionContext;

    @PostConstruct
    public void init() {
        // Initialize the ExecutorService and wrap it with DelegatingSecurityContextExecutorService
        executorService = Executors.newFixedThreadPool(10);
        delegatingExecutorService = new DelegatingSecurityContextExecutorService(executorService);
        executionContext = ExecutionContext.fromExecutor(delegatingExecutorService);
    }

    @PreDestroy
    public void destroy() {
        // Shut down the ExecutorService when the bean is destroyed
        executorService.shutdown();
    }

    public CompletableFuture<String> fetchSummaryAsync(RequestDTO requestDTO, String username) {
        // Use the shared ExecutionContext to fetch and save the summary asynchronously
        CompletableFuture<String> futureSummary =
                SummaryService.fetchAndSaveSummary(requestDTO.getUrl(), username, executionContext);

        return futureSummary;
    }

    public CompletableFuture<List<Summary>> fetchSummariesByUsernameAsync(String username) {
        // Use the shared ExecutionContext to fetch summaries by username asynchronously
        CompletableFuture<List<Summary>> futureAllSummaries =
                SummaryService.fetchSummariesByUsername(username, executionContext);

        return futureAllSummaries;
    }

}
