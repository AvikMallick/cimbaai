package com.cimba.summaryservice.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    USER("USER");
    private final String role;
}
