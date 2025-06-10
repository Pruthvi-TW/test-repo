```java
package com.mockuidai.controller;

import com.mockuidai.service.MockConfigService;
import com.mockuidai.util.TraceLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/uidai/internal/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MockConfigService configService;
    private final TraceLogger traceLogger;

    @GetMapping("/requests")
    public List<Map<String, Object>> getRequestHistory() {
        return traceLogger.getRequestHistory();
    }

    @PostMapping("/config")
    public void updateConfig(@RequestBody Map<String, Object> config) {
        configService.updateConfig(config);
    }

    @PostMapping("/reset")
    public void reset() {
        traceLogger.clearHistory();
        configService.resetConfig();
    }
}
```