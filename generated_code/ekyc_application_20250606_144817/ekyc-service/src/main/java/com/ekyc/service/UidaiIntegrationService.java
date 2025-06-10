package com.ekyc.service;

import com.ekyc.config.UidaiConfig;
import com.ekyc.exception.UidaiServiceException;
import com.ekyc.model.UidaiInitiateResponse;
import com.ekyc.model.UidaiOtpVerificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for integrating with the UIDAI (Unique Identification Authority of India) API.
 * Handles all external API calls to UIDAI for eKYC verification.
 */
@Service
public class UidaiIntegrationService {
    private static final Logger logger = LoggerFactory.getLogger(UidaiIntegrationService.class);
    
    private final RestTemplate restTemplate;
    private final UidaiConfig uidaiConfig;
    private final AuditService auditService;
    
    @Autowired
    public UidaiIntegrationService(RestTemplate restTemplate,
                                  UidaiConfig uidaiConfig,
                                  AuditService auditService) {
        this.restTemplate = restTemplate;
        this.uidaiConfig = uidaiConfig;
        this.auditService = auditService;
    }
    
    /**
     * Initiates the eKYC verification process with UIDAI.
     * 
     * @param idNumber The Aadhaar/VID number
     * @param idType The type of ID (AADHAAR or VID)
     * @param identityVerificationConsent Whether consent is given for identity verification
     * @param mobileEmailConsent Whether consent is given for accessing mobile/email
     * @return UidaiInitiateResponse containing the result of the initiation
     * @throws UidaiServiceException if there's an error communicating with UIDAI
     */
    @Retryable(value = {RestClientException.class}, 
               maxAttempts = 3, 
               backoff = @Backoff(delay = 1000, multiplier = 2))
    public UidaiInitiateResponse initiateEkyc(String idNumber, String idType, 
                                             boolean identityVerificationConsent,
                                             boolean mobileEmailConsent) {
        String transactionId = UUID.randomUUID().toString();
        String maskedId = auditService.maskAadhaarOrVid(idNumber);
        
        logger.info("Initiating eKYC with UIDAI for transaction: {}, ID type: {}", 
                transactionId, idType);
        
        try {
            // Prepare request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Transaction-ID", transactionId);
            headers.set("X-API-Key", uidaiConfig.getApiKey());
            
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("idNumber", idNumber);
            requestBody.put("idType", idType);
            requestBody.put("identityVerificationConsent", identityVerificationConsent);
            requestBody.put("mobileEmailConsent", mobileEmailConsent);
            requestBody.put("txnId", transactionId);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // Log the API call (with masked data)
            auditService.logApiCall("UIDAI_INITIATE_EKYC", transactionId, maskedId,
                    "Calling UIDAI to initiate eKYC");
            
            // Make the API call
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    uidaiConfig.getInitiateEkycUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );
            
            // Process the response
            Map<String, Object> responseBody = responseEntity.getBody();
            
            if (responseBody == null) {
                throw new UidaiServiceException("Received null response from UIDAI");
            }
            
            boolean success = Boolean.TRUE.equals(responseBody.get("success"));
            String referenceNumber = (String) responseBody.get("referenceNumber");
            String errorMessage = (String) responseBody.get("errorMessage");
            
            // Log the result
            if (success) {
                auditService.logSuccess("UIDAI_INITIATE_EKYC", transactionId, maskedId,
                        "UIDAI eKYC initiation successful with reference: " + referenceNumber);
            } else {
                auditService.logFailure("UIDAI_INITIATE_EKYC", transactionId, maskedId,
                        "UIDAI eKYC initiation failed: " + errorMessage);
            }
            
            return new UidaiInitiateResponse(success, referenceNumber, errorMessage);
        } catch (RestClientException rce) {
            auditService.logFailure("UIDAI_INITIATE_EKYC", transactionId, maskedId,
                    "REST client error during UIDAI eKYC initiation: " + rce.getMessage());
            
            logger.error("Error calling UIDAI API for eKYC initiation: {}", rce.getMessage());
            throw new UidaiServiceException("Failed to communicate with UIDAI for eKYC initiation", rce);
        } catch (Exception e) {
            auditService.logFailure("UIDAI_INITIATE_EKYC", transactionId, maskedId,
                    "Error during UIDAI eKYC initiation: " + e.getMessage());
            
            logger.error("Unexpected error during UIDAI eKYC initiation: {}", e.getMessage());
            throw new UidaiServiceException("Unexpected error during UIDAI eKYC initiation", e);
        }
    }
    
    /**
     * Verifies an OTP with UIDAI.
     * 
     * @param otp The OTP to verify
     * @param idNumber The Aadhaar/VID number
     * @param idType The type of ID (AADHAAR or VID)
     * @param referenceNumber The reference number from the eKYC initiation
     * @return UidaiOtpVerificationResponse containing the result of the verification
     * @throws UidaiServiceException if there's an error communicating with UIDAI
     */
    @Retryable(value = {RestClientException.class}, 
               maxAttempts = 2, 
               backoff = @Backoff(delay = 1000, multiplier = 2))
    public UidaiOtpVerificationResponse verifyOtp(String otp, String idNumber, String idType, 
                                                String referenceNumber) {
        String transactionId = UUID.randomUUID().toString();
        String maskedId = auditService.maskAadhaarOrVid(idNumber);
        String maskedOtp = "******"; // Always mask OTP in logs
        
        logger.info("Verifying OTP with UIDAI for transaction: {}, reference: {}", 
                transactionId, referenceNumber);
        
        try {
            // Prepare request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Transaction-ID", transactionId);
            headers.set("X-API-Key", uidaiConfig.getApiKey());
            
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("otp", otp);
            requestBody.put("idNumber", idNumber);
            requestBody.put("idType", idType);
            requestBody.put("referenceNumber", referenceNumber);
            requestBody.put("txnId", transactionId);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // Log the API call (with masked data)
            auditService.logApiCall("UIDAI_VERIFY_OTP", transactionId, maskedId,
                    "Calling UIDAI to verify OTP for reference: " + referenceNumber);
            
            // Make the API call
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    uidaiConfig.getVerifyOtpUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );
            
            // Process the response
            Map<String, Object> responseBody = responseEntity.getBody();
            
            if (responseBody == null) {
                throw new UidaiServiceException("Received null response from UIDAI for OTP verification");
            }
            
            boolean success = Boolean.TRUE.equals(responseBody.get("success"));
            String errorMessage = (String) responseBody.get("errorMessage");
            String responseData = responseBody.containsKey("data") ? 
                    responseBody.get("data").toString() : null;
            
            // Log the result
            if (success) {
                auditService.logSuccess("UIDAI_VERIFY_OTP", transactionId, maskedId,
                        "UIDAI OTP verification successful for reference: " + referenceNumber);
            } else {
                auditService.logFailure("UIDAI_VERIFY_OTP", transactionId, maskedId,
                        "UIDAI OTP verification failed: " + errorMessage);
            }
            
            return new UidaiOtpVerificationResponse(success, errorMessage, responseData);
        } catch (RestClientException rce) {
            auditService.logFailure("UIDAI_VERIFY_OTP", transactionId, maskedId,
                    "REST client error during UIDAI OTP verification: " + rce.getMessage());
            
            logger.error("Error calling UIDAI API for OTP verification: {}", rce.getMessage());
            throw new UidaiServiceException("Failed to communicate with UIDAI for OTP verification", rce);
        } catch (Exception e) {
            auditService.logFailure("UIDAI_VERIFY_OTP", transactionId, maskedId,
                    "Error during UIDAI OTP verification: " + e.getMessage());
            
            logger.error("Unexpected error during UIDAI OTP verification: {}", e.getMessage());
            throw new UidaiServiceException("Unexpected error during UIDAI OTP verification", e);
        }
    }
    
    /**
     * Checks if the UIDAI service is available.
     * 
     * @return true if the service is available, false otherwise
     */
    public boolean isServiceAvailable() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    uidaiConfig.getHealthCheckUrl(), Map.class);
            
            return response.getStatusCode().is2xxSuccessful() && 
                   response.getBody() != null && 
                   "UP".equals(response.getBody().get("status"));
        } catch (Exception e) {
            logger.warn("UIDAI service health check failed: {}", e.getMessage());
            return false;
        }
    }
}