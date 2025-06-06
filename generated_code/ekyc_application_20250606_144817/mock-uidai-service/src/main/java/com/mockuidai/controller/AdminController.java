package com.mockuidai.controller;

import com.mockuidai.dto.AdminConfigRequest;
import com.mockuidai.dto.AdminResponse;
import com.mockuidai.dto.RequestAuditRecord;
import com.mockuidai.service.MockConfigService;
import com.mockuidai.service.MockUidaiService;
import com.mockuidai.util.TraceLoggerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/uidai/internal/v1/admin")
@RequiredArgsConstructor
@Tag(name = "UIDAI Admin API", description = "Admin endpoints for configuration and monitoring")
public class AdminController {

    private final MockUidaiService mockUidaiService;
    private final MockConfigService mockConfigService;
    private final TraceLoggerUtil logger;

    @GetMapping("/requests")
    @Operation(summary = "Get request history", description = "Retrieves the history of requests with PII masked")
    public ResponseEntity<List<RequestAuditRecord>> getRequestHistory(
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        
        String requestTraceId = traceId != null ? traceId : UUID.randomUUID().toString();
        logger.info(requestTraceId, "Admin requested request history");
        
        List<RequestAuditRecord> history = mockUidaiService.getRequestHistory();
        
        return ResponseEntity.ok()
                .header("X-Trace-Id", requestTraceId)
                .body(history);
    }

    @PostMapping("/config")
    @Operation(summary = "Update configuration", description = "Updates the mock service configuration")
    public ResponseEntity<AdminResponse> updateConfig(
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId,
            @RequestBody AdminConfigRequest configRequest) {
        
        String requestTraceId = traceId != null ? traceId : UUID.randomUUID().toString();
        logger.info(requestTraceId, "Admin updating configuration: {}", configRequest);
        
        mockConfigService.updateConfig(configRequest);
        
        return ResponseEntity.ok()
                .header("X-Trace-Id", requestTraceId)
                .body(new AdminResponse("Configuration updated successfully"));
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset service state", description = "Resets the service state including request history and configuration")
    public ResponseEntity<AdminResponse> resetService(
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        
        String requestTraceId = traceId != null ? traceId : UUID.randomUUID().toString();
        logger.info(requestTraceId, "Admin requested service reset");
        
        mockUidaiService.clearRequestHistory();
        mockConfigService.resetConfig();
        
        return ResponseEntity.ok()
                .header("X-Trace-Id", requestTraceId)
                .body(new AdminResponse("Service state reset successfully"));
    }
}