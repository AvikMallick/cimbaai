package com.cimba.summaryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableAsync
//@ComponentScan({"com.avik.summaryservice", "com.avik"})
public class SummaryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SummaryServiceApplication.class, args);
    }

}