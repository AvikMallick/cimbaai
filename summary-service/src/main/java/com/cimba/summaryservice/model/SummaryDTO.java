package com.cimba.summaryservice.model;

import com.avik.summaryservice.Summary;
import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class SummaryDTO {
    private String id;
    private String url;
    private String username;
    private String content;
    private Timestamp timestamp;

    public static SummaryDTO convertToDTO(Summary summary) {
        SummaryDTO summaryDTO = new SummaryDTO();
        summaryDTO.setId(summary.id());
        summaryDTO.setUrl(summary.url());
        summaryDTO.setUsername(summary.username());
        summaryDTO.setContent(summary.content());
        summaryDTO.setTimestamp(summary.timestamp());
        return summaryDTO;
    }
}
