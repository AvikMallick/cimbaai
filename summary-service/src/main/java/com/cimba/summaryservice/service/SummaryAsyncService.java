package com.cimba.summaryservice.service;

import com.avik.summaryservice.SummaryService;
import com.cimba.summaryservice.model.RequestDTO;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Service;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SummaryAsyncService {

    public CompletableFuture<String> fetchSummaryAsync(RequestDTO requestDTO, String username) {
        // Create an ExecutorService with a fixed thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Wrap the ExecutorService in a DelegatingSecurityContextExecutorService
        ExecutorService delegatingExecutorService = new DelegatingSecurityContextExecutorService(executorService);

        // Create an ExecutionContext from the ExecutorService
        ExecutionContextExecutor executionContext = ExecutionContext.fromExecutor(delegatingExecutorService);

        // Fetch and save summary asynchronously
        CompletableFuture<String> futureSummary =
                SummaryService.fetchAndSaveSummary(requestDTO.getUrl(), username, executionContext);

        return futureSummary.whenComplete((result, ex) -> executorService.shutdown());
    }
}
