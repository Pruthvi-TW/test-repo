package com.mockuidai.service;

import com.mockuidai.dto.*;
import com.mockuidai.enums.UidaiResponseStatus;
import com.mockuidai.util.MockDataUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockUidaiService {

    private final MockConfigService configService;
    private final MockDataUtil mockDataUtil;
    
    // In-memory storage for OTP reference IDs
    private final Map<String, String> activeOtpReferences = new HashMap<>();

    public ResponseEntity<UidaiInitiateResponse> initiateOtp(UidaiInitiateRequest request) {
        // Simulate processing delay
        simulateLatency(300, 800);
        
        String aadhaarOrVid = request.getAadhaarOrVid();
        
        // Check for system error scenario (Aadhaar/VID starting with 999)
        if (aadhaarOrVid.startsWith("999")) {
            log.error("System error for Aadhaar/VID starting with 999");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(UidaiInitiateResponse.builder()
                            .status(UidaiResponseStatus.UIDAI_SERVICE_FAILURE.name())
                            .timestamp(Instant.now().toString())
                            .build());
        }
        
        // Check for OTP generation failure (Aadhaar/VID ending with odd digit)
        char lastChar = aadhaarOrVid.charAt(aadhaarOrVid.length() - 1);
        if (lastChar == '1' || lastChar == '3' || lastChar == '5' || lastChar == '7' || lastChar == '9') {
            log.info("OTP generation failed for Aadhaar/VID ending with odd digit");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(UidaiInitiateResponse.builder()
                            .status(UidaiResponseStatus.OTP_GENERATION_FAILED.name())
                            .timestamp(Instant.now().toString())
                            .build());
        }
        
        // Success scenario (Aadhaar/VID ending with even digit)
        String referenceId = "REF" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        
        // Store the Aadhaar/VID for this reference ID for later verification
        activeOtpReferences.put(referenceId, aadhaarOrVid);
        
        log.info("OTP initiated successfully with reference ID: {}", referenceId);
        return ResponseEntity.ok(UidaiInitiateResponse.builder()
                .status(UidaiResponseStatus.OTP_SENT.name())
                .referenceId(referenceId)
                .timestamp(Instant.now().toString())
                .build());
    }

    public ResponseEntity<UidaiVerifyResponse> verifyOtp(UidaiVerifyRequest request) {
        // Simulate processing delay
        simulateLatency(500, 1200);
        
        String referenceId = request.getReferenceId();
        String otp = request.getOtp();
        
        // Check for expired OTP scenario
        if ("REFEXPIRED001".equals(referenceId)) {
            log.info("Expired OTP for reference ID: {}", referenceId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(UidaiVerifyResponse.builder()
                            .status(UidaiResponseStatus.EXPIRED_OTP.name())
                            .timestamp(Instant.now().toString())
                            .build());
        }
        
        // Check if reference ID exists
        if (!activeOtpReferences.containsKey(referenceId)) {
            log.info("Invalid reference ID: {}", referenceId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(UidaiVerifyResponse.builder()
                            .status(UidaiResponseStatus.INVALID_REFERENCE_ID.name())
                            .timestamp(Instant.now().toString())
                            .build());
        }
        
        // Check for valid OTP
        if (!"123456".equals(otp)) {
            log.info("Invalid OTP provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(UidaiVerifyResponse.builder()
                            .status(UidaiResponseStatus.INVALID_OTP.name())
                            .timestamp(Instant.now().toString())
                            .build());
        }
        
        // Success scenario
        String aadhaarOrVid = activeOtpReferences.get(referenceId);
        KycData kycData = mockDataUtil.generateKycData(aadhaarOrVid);
        
        log.info("OTP verified successfully for reference ID: {}", referenceId);
        return ResponseEntity.ok(UidaiVerifyResponse.builder()
                .status(UidaiResponseStatus.VERIFIED.name())
                .kycData(kycData)
                .timestamp(Instant.now().toString())
                .build());
    }
    
    private void simulateLatency(int minMs, int maxMs) {
        try {
            int latency = ThreadLocalRandom.current().nextInt(minMs, maxMs + 1);
            
            // Apply latency multiplier from config if set
            double latencyMultiplier = configService.getLatencyMultiplier();
            int adjustedLatency = (int) (latency * latencyMultiplier);
            
            Thread.sleep(adjustedLatency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}