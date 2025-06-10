package com.ekyc.service;

import com.ekyc.exception.EkycException;
import com.ekyc.exception.ValidationException;
import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycResponse;
import com.ekyc.model.OtpVerificationRequest;
import com.ekyc.model.OtpVerificationResponse;
import com.ekyc.model.UidaiResponse;
import com.ekyc.model.VerificationStatus;
import com.ekyc.repository.EkycRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsible for handling the complete eKYC verification flow.
 * This includes request initiation, OTP verification, and response processing.
 */
@Service
public class EkycService {
    private static final Logger logger = LoggerFactory.getLogger(EkycService.class);
    
    private final ValidationService validationService;
    private final UidaiIntegrationService uidaiIntegrationService;
    private final OtpService otpService;
    private final AuditService auditService;
    private final SessionService sessionService;
    private final EkycRequestRepository ekycRequestRepository;
    
    @Autowired
    public EkycService(ValidationService validationService,
                      UidaiIntegrationService uidaiIntegrationService,
                      OtpService otpService,
                      AuditService auditService,
                      SessionService sessionService,
                      EkycRequestRepository ekycRequestRepository) {
        this.validationService = validationService;
        this.uidaiIntegrationService = uidaiIntegrationService;
        this.otpService = otpService;
        this.auditService = auditService;
        this.sessionService = sessionService;
        this.ekycRequestRepository = ekycRequestRepository;
    }
    
    /**
     * Initiates the eKYC verification process.
     * 
     * @param request The eKYC request containing Aadhaar/VID and consent information
     * @return EkycResponse with the status and reference number
     * @throws ValidationException if the request fails validation
     * @throws EkycException if there's an error during processing
     */
    @Transactional
    public EkycResponse initiateEkycVerification(EkycRequest request) {
        String maskedId = auditService.maskAadhaarOrVid(request.getIdNumber());
        logger.info("Initiating eKYC verification for ID: {}, Session: {}", 
                maskedId, request.getSessionId());
        
        try {
            // Validate the request
            validationService.validateEkycRequest(request);
            
            // Create session
            sessionService.createSession(request.getSessionId());
            
            // Generate reference number
            String referenceNumber = generateReferenceNumber();
            request.setReferenceNumber(referenceNumber);
            request.setStatus(VerificationStatus.INITIATED);
            request.setCreatedAt(LocalDateTime.now());
            
            // Save the request
            ekycRequestRepository.save(request);
            
            // Call UIDAI API to initiate eKYC
            UidaiResponse uidaiResponse = uidaiIntegrationService.initiateEkyc(
                    request.getIdNumber(), 
                    request.getIdType(), 
                    request.isIdentityVerificationConsent(),
                    request.isMobileEmailConsent());
            
            // Process UIDAI response
            if (uidaiResponse.isSuccess()) {
                request.setStatus(VerificationStatus.IN_PROGRESS);
                request.setUpdatedAt(LocalDateTime.now());
                ekycRequestRepository.save(request);
                
                auditService.logSuccess("eKYC initiation successful", request.getSessionId(), referenceNumber);
                
                return new EkycResponse(
                        referenceNumber,
                        VerificationStatus.IN_PROGRESS,
                        "OTP has been sent to your registered mobile number",
                        null
                );
            } else {
                request.setStatus(VerificationStatus.FAILED);
                request.setFailureReason(uidaiResponse.getErrorMessage());
                request.setUpdatedAt(LocalDateTime.now());
                ekycRequestRepository.save(request);
                
                auditService.logFailure("eKYC initiation failed", 
                        request.getSessionId(), 
                        referenceNumber, 
                        uidaiResponse.getErrorMessage());
                
                return new EkycResponse(
                        referenceNumber,
                        VerificationStatus.FAILED,
                        "Failed to initiate eKYC verification",
                        uidaiResponse.getErrorMessage()
                );
            }
        } catch (ValidationException ve) {
            auditService.logFailure("eKYC validation failed", 
                    request.getSessionId(), 
                    null, 
                    ve.getMessage());
            throw ve;
        } catch (Exception e) {
            auditService.logFailure("eKYC initiation error", 
                    request.getSessionId(), 
                    null, 
                    e.getMessage());
            throw new EkycException("Failed to process eKYC request: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifies the OTP for an existing eKYC request.
     * 
     * @param request The OTP verification request
     * @return OtpVerificationResponse with the verification status
     * @throws ValidationException if the request fails validation
     * @throws EkycException if there's an error during processing
     */
    @Transactional
    public OtpVerificationResponse verifyOtp(OtpVerificationRequest request) {
        logger.info("Processing OTP verification for reference: {}", request.getReferenceNumber());
        
        try {
            // Validate OTP format
            validationService.validateOtp(request.getOtp());
            
            // Retrieve eKYC request
            EkycRequest ekycRequest = ekycRequestRepository.findByReferenceNumber(request.getReferenceNumber())
                    .orElseThrow(() -> new ValidationException("Invalid reference number"));
            
            // Validate session
            sessionService.validateSession(ekycRequest.getSessionId());
            
            // Validate request status
            if (ekycRequest.getStatus() != VerificationStatus.IN_PROGRESS) {
                throw new ValidationException("eKYC request is not in a valid state for OTP verification");
            }
            
            // Call UIDAI API to verify OTP
            UidaiResponse uidaiResponse = uidaiIntegrationService.verifyOtp(
                    ekycRequest.getIdNumber(),
                    ekycRequest.getIdType(),
                    request.getOtp(),
                    request.getReferenceNumber());
            
            // Process UIDAI response
            if (uidaiResponse.isSuccess()) {
                // Update eKYC request status
                ekycRequest.setStatus(VerificationStatus.VERIFIED);
                ekycRequest.setUpdatedAt(LocalDateTime.now());
                ekycRequestRepository.save(ekycRequest);
                
                // Store OTP verification details
                otpService.storeOtpVerification(
                        request.getReferenceNumber(),
                        request.getOtp(),
                        true,
                        null);
                
                auditService.logSuccess("OTP verification successful", 
                        ekycRequest.getSessionId(), 
                        request.getReferenceNumber());
                
                return new OtpVerificationResponse(
                        request.getReferenceNumber(),
                        VerificationStatus.VERIFIED,
                        "OTP verification successful",
                        null
                );
            } else {
                // Update eKYC request status
                ekycRequest.setStatus(VerificationStatus.FAILED);
                ekycRequest.setFailureReason(uidaiResponse.getErrorMessage());
                ekycRequest.setUpdatedAt(LocalDateTime.now());
                ekycRequestRepository.save(ekycRequest);
                
                // Store OTP verification details
                otpService.storeOtpVerification(
                        request.getReferenceNumber(),
                        request.getOtp(),
                        false,
                        uidaiResponse.getErrorMessage());
                
                auditService.logFailure("OTP verification failed", 
                        ekycRequest.getSessionId(), 
                        request.getReferenceNumber(), 
                        uidaiResponse.getErrorMessage());
                
                return new OtpVerificationResponse(
                        request.getReferenceNumber(),
                        VerificationStatus.FAILED,
                        "OTP verification failed",
                        uidaiResponse.getErrorMessage()
                );
            }
        } catch (ValidationException ve) {
            auditService.logFailure("OTP validation failed", 
                    null, 
                    request.getReferenceNumber(), 
                    ve.getMessage());
            throw ve;
        } catch (Exception e) {
            auditService.logFailure("OTP verification error", 
                    null, 
                    request.getReferenceNumber(), 
                    e.getMessage());
            throw new EkycException("Failed to process OTP verification: " + e.getMessage(), e);
        }
    }
    
    /**
     * Retrieves the current status of an eKYC verification request.
     * 
     * @param referenceNumber The reference number of the eKYC request
     * @return EkycResponse with the current status
     * @throws ValidationException if the reference number is invalid
     * @throws EkycException if there's an error during processing
     */
    public EkycResponse getVerificationStatus(String referenceNumber) {
        logger.info("Retrieving verification status for reference: {}", referenceNumber);
        
        try {
            // Validate reference number
            if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
                throw new ValidationException("Reference number is required");
            }
            
            // Retrieve eKYC request
            EkycRequest ekycRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber)
                    .orElseThrow(() -> new ValidationException("Invalid reference number"));
            
            auditService.logInfo("Verification status retrieved", 
                    ekycRequest.getSessionId(), 
                    referenceNumber);
            
            return new EkycResponse(
                    referenceNumber,
                    ekycRequest.getStatus(),
                    getStatusMessage(ekycRequest.getStatus()),
                    ekycRequest.getFailureReason()
            );
        } catch (ValidationException ve) {
            auditService.logFailure("Status retrieval validation failed", 
                    null, 
                    referenceNumber, 
                    ve.getMessage());
            throw ve;
        } catch (Exception e) {
            auditService.logFailure("Status retrieval error", 
                    null, 
                    referenceNumber, 
                    e.getMessage());
            throw new EkycException("Failed to retrieve verification status: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates a unique reference number for an eKYC request.
     * 
     * @return A unique reference number
     */
    private String generateReferenceNumber() {
        return "EKYC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Returns a user-friendly message based on the verification status.
     * 
     * @param status The verification status
     * @return A user-friendly message
     */
    private String getStatusMessage(VerificationStatus status) {
        switch (status) {
            case INITIATED:
                return "eKYC verification initiated";
            case IN_PROGRESS:
                return "OTP verification pending";
            case VERIFIED:
                return "eKYC verification completed successfully";
            case FAILED:
                return "eKYC verification failed";
            default:
                return "Unknown status";
        }
    }
}