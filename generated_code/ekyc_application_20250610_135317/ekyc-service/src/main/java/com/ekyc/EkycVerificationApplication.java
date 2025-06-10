```java
package com.ekyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main application class for the eKYC Verification System.
 * Configures and launches the Spring Boot application.
 */
@SpringBootApplication
@EntityScan(basePackages = "com.ekyc.model")
@EnableJpaRepositories(basePackages = "com.ekyc.repository")
public class EkycVerificationApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EkycVerificationApplication.class, args);
    }
}