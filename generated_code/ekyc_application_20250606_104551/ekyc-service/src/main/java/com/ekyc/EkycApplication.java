```java
package com.ekyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the eKYC service.
 * Enables JPA auditing, transaction management, and component scanning.
 *
 * @author eKYC Team
 * @version 1.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EntityScan(basePackages = "com.ekyc.entity")
@EnableJpaRepositories(basePackages = "com.ekyc.repository")
public class EkycApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EkycApplication.class, args);
    }
}
```