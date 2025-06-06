package com.ekyc.service;

import com.ekyc.config.UidaiConfig;
import com.ekyc.exception.UidaiServiceException;
import com.ekyc.model.IdType;
import com.ekyc.model.UidaiResponse;
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
     * @param idNumber The Aadhaar or VID number
     * @param idType The type of ID (AADHAAR or VID)
     * @param identityVerificationConsent Whether consent for identity verification is given
     * @param mobileEmailConsent Whether consent for accessing mobile/email is given
     * @return UidaiResponse containing the result of the API call
     * @throws UidaiServiceException if there's an error during the API call
     */
    @Retryable(value = {RestClientException.class}, 
              maxAttempts = 3, 
              backoff = @Backoff(delay = 1000, multiplier = 2))
    public UidaiResponse initiateEkyc(String idNumber, IdType idType, 
                                     boolean identityVerificationConsent,
                                     boolean mobileEmailConsent) {
        String maskedId = auditService.maskAadhaarOrVid(idNumber);
        logger.info("Initiating eKYC with UIDAI for ID: {}, Type: {}", maskedId, idType);
        
        try {
            // Prepare request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", uidaiConfig.getApiKey());
            
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("idNumber", idNumber);
            requestBody.put("idType", idType.toString());
            requestBody.put("identityVerificationConsent", identityVerificationConsent);
            requestBody.put("mobileEmailConsent", mobileEmailConsent);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // Make API call
            ResponseEntity<UidaiResponse> responseEntity = restTemplate.exchange(
                    uidaiConfig.getInitiateEkycUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    UidaiResponse.class);
            
            UidaiResponse response = responseEntity.getBody();
            
            if (response != null && response.isSuccess()) {
                auditService.logSuccess("UIDAI eKYC initiation successful", null, null);
            } else {
                String errorMessage = response != null ? response.getErrorMessage() : "No response from UIDAI";
                auditService.logFailure("UIDAI eKYC initiation failed", null, null, errorMessage);
            }
            
            return response != null ? response : new UidaiResponse(false, "No response from UIDAI", null);
        } catch (RestClientException rce) {
            auditService.logFailure("UIDAI API call failed", null, null, rce.getMessage());
            logger.error("Error calling UIDAI API: {}", rce.getMessage(), rce);
            throw new UidaiServiceException("Failed to communicate with UIDAI: " + rce.getMessage(), rce);
        } catch (Exception e) {
            auditService.logFailure("UIDAI integration error", null, null, e.getMessage());
            logger.error("Unexpected error during UIDAI integration: {}", e.getMessage(), e);
            throw new UidaiServiceException("Unexpected error during UIDAI integration: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifies an OTP with UIDAI for eKYC verification.
     * 
     * @param idNumber The Aadhaar or VID number
     * @param idType The type of ID (AADHAAR or VID)
     * @param otp The OTP to verify
     * @param referenceNumber The reference number of the eKYC request
     * @return UidaiResponse containing the result of the API call
     * @throws UidaiServiceException if there's an error during the API call
     */
    @Retryable(value = {RestClientException.class}, 
              maxAttempts = 3, 
              backoff = @Backoff(delay = 1000, multiplier = 2))
    public UidaiResponse verifyOtp(String idNumber, IdType idType, String otp, String referenceNumber) {
        String maskedId = auditService.maskAadhaarOrVid(idNumber);
        String maskedOtp = auditService.maskOtp(otp);
        logger.info("Verifying OTP with UIDAI for ID: {}, Reference: {}, OTP: {}", 
                maskedId, referenceNumber, maskedOtp);
        
        try {
            // Prepare request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", uidaiConfig.getApiKey());
            
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("idNumber", idNumber);
            requestBody.put("idType", idType.toString());
            requestBody.put("otp", otp);
            requestBody.put("referenceNumber", referenceNumber);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // Make API call
            ResponseEntity<UidaiResponse> responseEntity = restTemplate.exchange(
                    uidaiConfig.getVerifyOtpUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    UidaiResponse.class);
            
            UidaiResponse response = responseEntity.getBody();
            
            if (response != null && response.isSuccess()) {
                auditService.logSuccess("UIDAI OTP verification successful", null, referenceNumber);
            } else {
                String errorMessage = response != null ? response.getErrorMessage() : "No response from UIDAI";
                auditService.logFailure("UIDAI OTP verification failed", null, referenceNumber, errorMessage);
            }
            
            return response != null ? response : new UidaiResponse(false, "No response from UIDAI", null);
        } catch (RestClientException rce) {
            auditService.logFailure("UIDAI API call failed", null, referenceNumber, rce.getMessage());
            logger.error("Error calling UIDAI API: {}", rce.getMessage(), rce);
            throw new UidaiServiceException("Failed to communicate with UIDAI: " + rce.getMessage(), rce);
        } catch (Exception e) {
            auditService.logFailure("UIDAI integration error", null, referenceNumber, e.getMessage());
            logger.error("Unexpected error during UIDAI integration: {}", e.getMessage(), e);
            throw new UidaiServiceException("Unexpected error during UIDAI integration: " + e.getMessage(), e);
        }
    }
    
    /**
     * Checks if the UIDAI service is available.
     * 
     * @return true if the service is available, false otherwise
     */
    public boolean isServiceAvailable() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    uidaiConfig.getHealthCheckUrl(), 
                    String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.warn("UIDAI service health check failed: {}", e.getMessage());
            return false;
        }
    }
}