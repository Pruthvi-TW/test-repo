```java
package com.ekyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the eKYC Verification Service.
 * Enables JPA auditing and transaction management.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
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
```

These are the core configuration files. Would you like me to continue with the model package (entities) next?