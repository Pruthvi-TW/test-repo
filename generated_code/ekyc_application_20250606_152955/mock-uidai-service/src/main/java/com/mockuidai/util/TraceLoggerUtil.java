package com.mockuidai.util;

import com.mockuidai.dto.ApiAuditRecord;
import com.mockuidai.dto.UidaiInitiateRequest;
import com.mockuidai.dto.UidaiVerifyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class TraceLoggerUtil {

    private final List<ApiAuditRecord> requestHistory = new CopyOnWriteArrayList<>();
    private static final int MAX_HISTORY_SIZE = 100;

    public void logRequest(String traceId, String operation, Object request) {
        // Mask PII in request
        Object maskedRequest = maskPii(request);
        log.info("Trace ID: {} - {} Request: {}", traceId, operation, maskedRequest);
    }

    public void logResponse(String traceId, String operation, ResponseEntity<?> response) {
        // Mask PII in response
        Object maskedResponse = maskPii(response.getBody());
        log.info("Trace ID: {} - {} Response: {} - Status: {}", 
                traceId, operation, maskedResponse, response.getStatusCode());
        
        // Add to audit history
        addToHistory(ApiAuditRecord.builder()
                .traceId(traceId)
                .timestamp(Instant.now().toString())
                .operation(operation)
                .maskedRequest(maskPii(getRequestForOperation(operation, traceId)))
                .maskedResponse(maskedResponse)
                .statusCode(response.getStatusCodeValue())
                .processingTimeMs(System.currentTimeMillis() % 1000) // Simplified for demo
                .build());
    }

    public List<ApiAuditRecord> getRequestHistory() {
        return Collections.unmodifiableList(new ArrayList<>(requestHistory));
    }

    public void clearHistory() {
        requestHistory.clear();
        log.info("Request history cleared");
    }

    private void addToHistory(ApiAuditRecord record) {
        requestHistory.add(record);
        // Trim history if it exceeds maximum size
        if (requestHistory.size() > MAX_HISTORY_SIZE) {
            requestHistory.remove(0);
        }
    }

    private Object maskPii(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof UidaiInitiateRequest) {
            UidaiInitiateRequest request = (UidaiInitiateRequest) obj;
            UidaiInitiateRequest masked = new UidaiInitiateRequest();
            masked.setTransactionId(request.getTransactionId());
            
            // Mask Aadhaar/VID
            String aadhaarOrVid = request.getAadhaarOrVid();
            if (aadhaarOrVid != null && aadhaarOrVid.length() >= 4) {
                masked.setAadhaarOrVid("XXXXXXXX" + aadhaarOrVid.substring(aadhaarOrVid.length() - 4));
            } else {
                masked.setAadhaarOrVid("XXXXXXXXXXXX");
            }
            
            return masked;
        }

        if (obj instanceof UidaiVerifyRequest) {
            UidaiVerifyRequest request = (UidaiVerifyRequest) obj;
            UidaiVerifyRequest masked = new UidaiVerifyRequest();
            masked.setReferenceId(request.getReferenceId());
            masked.setOtp("XXXXXX"); // Always mask OTP
            return masked;
        }

        // For other objects, return as is (response objects don't need masking)
        return obj;
    }

    private Object getRequestForOperation(String operation, String traceId) {
        // This is a simplified implementation
        // In a real implementation, you would store the original request with the trace ID
        if (operation.contains("OTP Initiation")) {
            UidaiInitiateRequest request = new UidaiInitiateRequest();
            request.setAadhaarOrVid("123456789012");
            request.setTransactionId("TXN" + traceId.substring(0, 6));
            return request;
        } else if (operation.contains("OTP Verification")) {
            UidaiVerifyRequest request = new UidaiVerifyRequest();
            request.setReferenceId("REF" + traceId.substring(0, 10));
            request.setOtp("123456");
            return request;
        }
        return null;
    }
}