package com.cimba.summaryservice.model;

import lombok.*;

@Data
@Builder
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private String url;
}
