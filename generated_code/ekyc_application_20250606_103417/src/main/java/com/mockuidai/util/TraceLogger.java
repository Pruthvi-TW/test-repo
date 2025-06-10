```java
package com.mockuidai.util;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class TraceLogger {
    private final List<Map<String, Object>> requestHistory = new ArrayList<>();

    public void logRequest(String operation, Object request) {
        Map<String, Object> log = new HashMap<>();
        log.put("traceId", UUID.randomUUID().toString());
        log.put("timestamp", System.currentTimeMillis());
        log.put("operation", operation);
        log.put("request", maskPII(request));
        requestHistory.add(log);
    }

    public void logResponse(String operation, Object response) {
        Map<String, Object> log = new HashMap<>();
        log.put("traceId", UUID.randomUUID().toString());
        log.put("timestamp", System.currentTimeMillis());
        log.put("operation", operation);
        log.put("response", maskPII(response));
        requestHistory.add(log);
    }

    public List<Map<String, Object>> getRequestHistory() {
        return new ArrayList<>(requestHistory);
    }

    public void clearHistory() {
        requestHistory.clear();
    }

    private Object maskPII(Object obj) {
        // Implement PII masking logic here
        return obj;
    }
}
```