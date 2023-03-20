package com.affliate.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class AffiliateBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(AffiliateBatchApplication.class, args);
    }

}
