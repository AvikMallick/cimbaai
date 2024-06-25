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
            // Block and get the result
            Summary summary = futureSummary.get();
            // Convert the Summary to a SummaryDTO
            SummaryDTO summaryDTO = SummaryDTO.convertToDTO(summary);
            return ResponseEntity.ok(summaryDTO);
        } catch (Exception e) {
            logger.error("Error fetching summary. Server Error: " + e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/get-summary-history")
    public ResponseEntity<List<SummaryDTO>> getSummaryHistory() {
        // Get the authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Fetch summaries by username asynchronously using the async service
        CompletableFuture<List<Summary>> futureAllSummaries =
                summaryAsyncService.fetchSummariesByUsernameAsync(username);

        try {
            // Block and get the result
            List<Summary> summaries = futureAllSummaries.get();
            // Convert the List<Summary> to List<SummaryDTO>
            List<SummaryDTO> summaryDTOList =
                    summaries.stream()
                    .map(SummaryDTO::convertToDTO)
                    .toList();
            return ResponseEntity.ok(summaryDTOList);
        } catch (Exception e) {
            logger.error("Error fetching summary history. Server Error: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}