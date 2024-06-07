package com.cimba.summaryservice.model;

import lombok.*;

@Data
@Builder
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class TokenValidationResponseDTO {
    Boolean valid;
}
