package com.mockuidai.controller;

import com.mockuidai.dto.UidaiInitiateRequest;
import com.mockuidai.dto.UidaiInitiateResponse;
import com.mockuidai.dto.UidaiVerifyRequest;
import com.mockuidai.dto.UidaiVerifyResponse;
import com.mockuidai.service.MockUidaiService;
import com.mockuidai.util.TraceLoggerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/uidai/internal/v1/ekyc")
@RequiredArgsConstructor
@Tag(name = "UIDAI eKYC API", description = "Mock UIDAI eKYC API endpoints")
public class MockUidaiController {

    private final MockUidaiService mockUidaiService;
    private final TraceLoggerUtil logger;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate OTP for eKYC", description = "Initiates OTP generation for Aadhaar/VID verification")
    public ResponseEntity<UidaiInitiateResponse> initiateOtp(
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId,
            @Valid @RequestBody UidaiInitiateRequest request) {
        
        String requestTraceId = traceId != null ? traceId : UUID.randomUUID().toString();
        logger.info(requestTraceId, "Received OTP initiation request for aadhaar/VID: {}", 
                logger.maskPii(request.getAadhaarOrVid()));
        
        ResponseEntity<UidaiInitiateResponse> response = mockUidaiService.initiateOtp(requestTraceId, request);
        
        logger.info(requestTraceId, "Completed OTP initiation with status: {}", 
                response.getBody() != null ? response.getBody().getStatus() : "N/A");
        
        return ResponseEntity.status(response.getStatusCode())
                .header("X-Trace-Id", requestTraceId)
                .body(response.getBody());
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify OTP and retrieve eKYC", description = "Verifies OTP and returns eKYC data if successful")
    public ResponseEntity<UidaiVerifyResponse> verifyOtp(
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId,
            @Valid @RequestBody UidaiVerifyRequest request) {
        
        String requestTraceId = traceId != null ? traceId : UUID.randomUUID().toString();
        logger.info(requestTraceId, "Received OTP verification request for referenceId: {}, OTP: {}", 
                request.getReferenceId(), logger.maskPii(request.getOtp()));
        
        ResponseEntity<UidaiVerifyResponse> response = mockUidaiService.verifyOtp(requestTraceId, request);
        
        logger.info(requestTraceId, "Completed OTP verification with status: {}", 
                response.getBody() != null ? response.getBody().getStatus() : "N/A");
        
        return ResponseEntity.status(response.getStatusCode())
                .header("X-Trace-Id", requestTraceId)
                .body(response.getBody());
    }
}