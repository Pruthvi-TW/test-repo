package com.identityverification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for identityverification&kycservices.
 * 
 * This class serves as the entry point for the Spring Boot application.
 * It enables JPA repositories and transaction management.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class Application {

    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
