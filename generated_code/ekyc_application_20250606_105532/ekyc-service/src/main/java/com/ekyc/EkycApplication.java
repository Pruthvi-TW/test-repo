```java
package com.ekyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the eKYC service.
 * Enables JPA auditing and transaction management.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class EkycApplication {

    public static void main(String[] args) {
        SpringApplication.run(EkycApplication.class, args);
    }
}
```