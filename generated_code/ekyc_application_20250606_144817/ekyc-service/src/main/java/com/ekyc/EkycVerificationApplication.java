package com.ekyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the eKYC Verification System.
 * This application provides electronic Know Your Customer verification services
 * by integrating with UIDAI Aadhaar verification APIs.
 * 
 * @author eKYC Team
 * @version 1.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class EkycVerificationApplication {

    /**
     * Main method that starts the eKYC Verification application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(EkycVerificationApplication.class, args);
    }
}