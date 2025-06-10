package com.mockuidai.service;

import com.mockuidai.dto.*;
import com.mockuidai.enums.UidaiResponseStatus;
import com.mockuidai.util.MockDataUtil;
import com.mockuidai.util.TraceLoggerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class MockUidaiService {

    private final MockConfigService configService;
    private final TraceLoggerUtil logger;
    private final MockDataUtil mockDataUtil;
    
    // In-memory storage for OTP references
    private final Map<String, String> otpReferences = new ConcurrentHashMap<>();
    
    // Request audit history
    private final List<RequestAuditRecord> requestHistory = new CopyOnWriteArrayList<>();

    public ResponseEntity<UidaiInitiateResponse> initiateOtp(String traceId, UidaiInitiateRequest request) {
        // Add artificial delay for realism
        simulateLatency();
        
        // Record request for audit
        recordRequest(traceId, "initiate", request);
        
        // Check for system error simulation (Aadhaar/VID starting with 999)
        if (request.getAadhaarOrVid().startsWith("999")) {
            logger.error(traceId, "System error simulation for aadhaar/VID: {}", 
                    logger.maskPii(request.getAadhaarOrVid()));
            
            UidaiInitiateResponse errorResponse = UidaiInitiateResponse.builder()
                    .status(UidaiResponseStatus.UIDAI_SERVICE_FAILURE.name())
                    .timestamp(Instant.now())
                    .build();
            
            recordResponse(traceId, errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
        
        // Check if Aadhaar/VID ends with odd digit (failure case)
        char lastChar = request.getAadhaarOrVid().charAt(request.getAadhaarOrVid().length() - 1);
        if (Character.getNumericValue(lastChar) % 2 != 0) {
            logger.info(traceId, "OTP generation failed for aadhaar/VID ending with odd digit: {}", 
                    logger.maskPii(request.getAadhaarOrVid()));
            
            UidaiInitiateResponse failureResponse = UidaiInitiateResponse.builder()
                    .status(UidaiResponseStatus.OTP_GENERATION_FAILED.name())
                    .timestamp(Instant.now())
                    .build();
            
            recordResponse(traceId, failureResponse, HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureResponse);
        }
        
        // Success case - Aadhaar/VID ends with even digit
        String referenceId = "REF" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        
        // Store the reference for later verification
        otpReferences.put(referenceId, request.getAadhaarOrVid());
        
        UidaiInitiateResponse successResponse = UidaiInitiateResponse.builder()
                .status(UidaiResponseStatus.OTP_SENT.name())
                .referenceId(referenceId)
                .timestamp(Instant.now())
                .build();
        
        logger.info(traceId, "OTP initiated successfully with referenceId: {}", referenceId);
        recordResponse(traceId, successResponse, HttpStatus.OK);
        return ResponseEntity.ok(successResponse);
    }

    public ResponseEntity<UidaiVerifyResponse> verifyOtp(String traceId, UidaiVerifyRequest request) {
        // Add artificial delay for realism
        simulateLatency();
        
        // Record request for audit
        recordRequest(traceId, "verify", request);
        
        // Check for expired OTP case
        if ("REFEXPIRED001".equals(request.getReferenceId())) {
            logger.info(traceId, "Expired OTP for referenceId: {}", request.getReferenceId());
            
            UidaiVerifyResponse expiredResponse = UidaiVerifyResponse.builder()
                    .status(UidaiResponseStatus.EXPIRED_OTP.name())
                    .timestamp(Instant.now())
                    .build();
            
            recordResponse(traceId, expiredResponse, HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(expiredResponse);
        }
        
        // Check if reference exists
        if (!otpReferences.containsKey(request.getReferenceId())) {
            logger.info(traceId, "Invalid reference ID: {}", request.getReferenceId());
            
            UidaiVerifyResponse invalidRefResponse = UidaiVerifyResponse.builder()
                    .status(UidaiResponseStatus.INVALID_REFERENCE_ID.name())
                    .timestamp(Instant.now())
                    .build();
            
            recordResponse(traceId, invalidRefResponse, HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invalidRefResponse);
        }
        
        // Check if OTP is valid (123456)
        if (!"123456".equals(request.getOtp())) {
            logger.info(traceId, "Invalid OTP provided for referenceId: {}", request.getReferenceId());
            
            UidaiVerifyResponse invalidOtpResponse = UidaiVerifyResponse.builder()
                    .status(UidaiResponseStatus.INVALID_OTP.name())
                    .timestamp(Instant.now())
                    .build();
            
            recordResponse(traceId, invalidOtpResponse, HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invalidOtpResponse);
        }
        
        // Success case - OTP is valid
        String aadhaarOrVid = otpReferences.get(request.getReferenceId());
        KycData kycData = mockDataUtil.generateKycData(aadhaarOrVid);
        
        UidaiVerifyResponse successResponse = UidaiVerifyResponse.builder()
                .status(UidaiResponseStatus.VERIFIED.name())
                .kycData(kycData)
                .timestamp(Instant.now())
                .build();
        
        logger.info(traceId, "OTP verified successfully for referenceId: {}", request.getReferenceId());
        recordResponse(traceId, successResponse, HttpStatus.OK);
        return ResponseEntity.ok(successResponse);
    }

    public List<RequestAuditRecord> getRequestHistory() {
        return new ArrayList<>(requestHistory);
    }

    public void clearRequestHistory() {
        requestHistory.clear();
        otpReferences.clear();
        logger.info("System", "Request history and OTP references cleared");
    }

    private void recordRequest(String traceId, String operation, Object request) {
        RequestAuditRecord record = new RequestAuditRecord();
        record.setTraceId(traceId);
        record.setTimestamp(Instant.now());
        record.setOperation(operation);
        record.setRequest(request);
        requestHistory.add(record);
    }

    private void recordResponse(String traceId, Object response, HttpStatus status) {
        // Find the matching request record and update it
        requestHistory.stream()
                .filter(record -> record.getTraceId().equals(traceId) && record.getResponse() == null)
                .findFirst()
                .ifPresent(record -> {
                    record.setResponse(response);
                    record.setStatus(status.value());
                    record.setResponseTimestamp(Instant.now());
                });
    }

    private void simulateLatency() {
        int latencyMs = configService.getConfig().getSimulatedLatencyMs();
        if (latencyMs > 0) {
            try {
                Thread.sleep(latencyMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}