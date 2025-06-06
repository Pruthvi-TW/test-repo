```java
package com.ekyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EkycApplication {
    public static void main(String[] args) {
        SpringApplication.run(EkycApplication.class, args);
    }
}
```