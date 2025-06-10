package com.ekyc.service;

import com.ekyc.exception.EkycException;
import com.ekyc.exception.ValidationException;
import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycResponse;
import com.ekyc.model.OtpVerificationRequest;
import com.ekyc.model.OtpVerificationResponse;
import com.ekyc.model.UidaiInitiateResponse;
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
 * Service responsible for handling the complete eKYC verification process.
 * Implements the three-phase business flow:
 * 1. eKYC Request Initiation
 * 2. OTP Verification
 * 3. Response Processing
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
     * Phase 1: Initiates the eKYC verification process.
     * 
     * @param request The eKYC request containing Aadhaar/VID and consent information
     * @return EkycResponse with the status and reference number
     * @throws ValidationException if the request fails validation
     * @throws EkycException if there's an error during the eKYC initiation process
     */
    @Transactional
    public EkycResponse initiateEkycVerification(EkycRequest request) {
        String maskedId = auditService.maskAadhaarOrVid(request.getIdNumber());
        String requestId = UUID.randomUUID().toString();
        
        logger.info("Initiating eKYC verification for request ID: {}, masked ID: {}", requestId, maskedId);
        
        try {
            // Validate the request
            validationService.validateEkycRequest(request);
            
            // Create session for this request
            String sessionId = sessionService.createSession(request.getSessionId());
            request.setSessionId(sessionId);
            
            // Set initial status
            request.setStatus(VerificationStatus.INITIATED);
            request.setRequestId(requestId);
            request.setCreatedAt(LocalDateTime.now());
            
            // Save the request
            EkycRequest savedRequest = ekycRequestRepository.save(request);
            
            // Call UIDAI API to initiate eKYC
            UidaiInitiateResponse uidaiResponse = uidaiIntegrationService.initiateEkyc(
                    request.getIdNumber(), 
                    request.getIdType(), 
                    request.isIdentityVerificationConsent(),
                    request.isMobileEmailConsent());
            
            // Update request status based on UIDAI response
            if (uidaiResponse.isSuccess()) {
                savedRequest.setStatus(VerificationStatus.IN_PROGRESS);
                savedRequest.setReferenceNumber(uidaiResponse.getReferenceNumber());
                savedRequest.setUpdatedAt(LocalDateTime.now());
                ekycRequestRepository.save(savedRequest);
                
                auditService.logSuccess("eKYC_INITIATION", requestId, maskedId, 
                        "eKYC initiated successfully with reference: " + uidaiResponse.getReferenceNumber());
                
                return new EkycResponse(
                        true,
                        uidaiResponse.getReferenceNumber(),
                        VerificationStatus.IN_PROGRESS,
                        "OTP has been sent to your registered mobile number",
                        null
                );
            } else {
                savedRequest.setStatus(VerificationStatus.FAILED);
                savedRequest.setFailureReason(uidaiResponse.getErrorMessage());
                savedRequest.setUpdatedAt(LocalDateTime.now());
                ekycRequestRepository.save(savedRequest);
                
                auditService.logFailure("eKYC_INITIATION", requestId, maskedId, 
                        "eKYC initiation failed: " + uidaiResponse.getErrorMessage());
                
                return new EkycResponse(
                        false,
                        requestId,
                        VerificationStatus.FAILED,
                        "Failed to initiate eKYC verification",
                        uidaiResponse.getErrorMessage()
                );
            }
        } catch (ValidationException ve) {
            auditService.logFailure("eKYC_INITIATION", requestId, maskedId, 
                    "Validation failed: " + ve.getMessage());
            throw ve;
        } catch (Exception e) {
            auditService.logFailure("eKYC_INITIATION", requestId, maskedId, 
                    "Error during eKYC initiation: " + e.getMessage());
            throw new EkycException("Failed to process eKYC request", e);
        }
    }
    
    /**
     * Phase 2: Verifies the OTP for an existing eKYC request.
     * 
     * @param otpRequest The OTP verification request
     * @return OtpVerificationResponse with the verification status
     * @throws ValidationException if the OTP request fails validation
     * @throws EkycException if there's an error during the OTP verification process
     */
    @Transactional
    public OtpVerificationResponse verifyOtp(OtpVerificationRequest otpRequest) {
        logger.info("Processing OTP verification for reference: {}", otpRequest.getReferenceNumber());
        
        try {
            // Validate OTP format
            validationService.validateOtpRequest(otpRequest);
            
            // Retrieve the eKYC request
            EkycRequest ekycRequest = ekycRequestRepository.findByReferenceNumber(otpRequest.getReferenceNumber())
                    .orElseThrow(() -> new ValidationException("Invalid reference number"));
            
            String maskedId = auditService.maskAadhaarOrVid(ekycRequest.getIdNumber());
            
            // Verify session is still valid
            sessionService.validateSession(ekycRequest.getSessionId());
            
            // Verify the request is in the correct state for OTP verification
            if (ekycRequest.getStatus() != VerificationStatus.IN_PROGRESS) {
                auditService.logFailure("OTP_VERIFICATION", ekycRequest.getRequestId(), maskedId,
                        "Invalid request state for OTP verification: " + ekycRequest.getStatus());
                throw new ValidationException("This request is not in a valid state for OTP verification");
            }
            
            // Call OTP service to verify with UIDAI
            boolean otpVerified = otpService.verifyOtp(
                    otpRequest.getOtp(),
                    ekycRequest.getIdNumber(),
                    ekycRequest.getIdType(),
                    otpRequest.getReferenceNumber()
            );
            
            // Update request status based on verification result
            if (otpVerified) {
                ekycRequest.setStatus(VerificationStatus.VERIFIED);
                ekycRequest.setUpdatedAt(LocalDateTime.now());
                ekycRequestRepository.save(ekycRequest);
                
                auditService.logSuccess("OTP_VERIFICATION", ekycRequest.getRequestId(), maskedId,
                        "OTP verification successful");
                
                return new OtpVerificationResponse(
                        true,
                        otpRequest.getReferenceNumber(),
                        VerificationStatus.VERIFIED,
                        "OTP verification successful",
                        null
                );
            } else {
                ekycRequest.setStatus(VerificationStatus.FAILED);
                ekycRequest.setFailureReason("Invalid OTP");
                ekycRequest.setUpdatedAt(LocalDateTime.now());
                ekycRequestRepository.save(ekycRequest);
                
                auditService.logFailure("OTP_VERIFICATION", ekycRequest.getRequestId(), maskedId,
                        "OTP verification failed: Invalid OTP");
                
                return new OtpVerificationResponse(
                        false,
                        otpRequest.getReferenceNumber(),
                        VerificationStatus.FAILED,
                        "OTP verification failed",
                        "Invalid OTP"
                );
            }
        } catch (ValidationException ve) {
            auditService.logFailure("OTP_VERIFICATION", "N/A", "N/A", 
                    "Validation failed: " + ve.getMessage());
            throw ve;
        } catch (Exception e) {
            auditService.logFailure("OTP_VERIFICATION", "N/A", "N/A", 
                    "Error during OTP verification: " + e.getMessage());
            throw new EkycException("Failed to process OTP verification", e);
        }
    }
    
    /**
     * Phase 3: Retrieves the current status of an eKYC verification request.
     * 
     * @param referenceNumber The reference number of the eKYC request
     * @return EkycResponse with the current status
     * @throws ValidationException if the reference number is invalid
     * @throws EkycException if there's an error retrieving the status
     */
    @Transactional(readOnly = true)
    public EkycResponse getVerificationStatus(String referenceNumber) {
        logger.info("Retrieving verification status for reference: {}", referenceNumber);
        
        try {
            // Validate reference number
            if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
                throw new ValidationException("Reference number cannot be empty");
            }
            
            // Retrieve the eKYC request
            EkycRequest ekycRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber)
                    .orElseThrow(() -> new ValidationException("Invalid reference number"));
            
            String maskedId = auditService.maskAadhaarOrVid(ekycRequest.getIdNumber());
            
            // Verify session is still valid
            sessionService.validateSession(ekycRequest.getSessionId());
            
            auditService.logSuccess("STATUS_CHECK", ekycRequest.getRequestId(), maskedId,
                    "Status check successful: " + ekycRequest.getStatus());
            
            return new EkycResponse(
                    ekycRequest.getStatus() == VerificationStatus.VERIFIED,
                    referenceNumber,
                    ekycRequest.getStatus(),
                    getStatusMessage(ekycRequest.getStatus()),
                    ekycRequest.getFailureReason()
            );
        } catch (ValidationException ve) {
            auditService.logFailure("STATUS_CHECK", "N/A", "N/A", 
                    "Validation failed: " + ve.getMessage());
            throw ve;
        } catch (Exception e) {
            auditService.logFailure("STATUS_CHECK", "N/A", "N/A", 
                    "Error during status check: " + e.getMessage());
            throw new EkycException("Failed to retrieve verification status", e);
        }
    }
    
    /**
     * Helper method to get a user-friendly status message based on the verification status.
     * 
     * @param status The verification status
     * @return A user-friendly status message
     */
    private String getStatusMessage(VerificationStatus status) {
        switch (status) {
            case INITIATED:
                return "eKYC verification has been initiated";
            case IN_PROGRESS:
                return "eKYC verification is in progress, waiting for OTP verification";
            case VERIFIED:
                return "eKYC verification completed successfully";
            case FAILED:
                return "eKYC verification failed";
            default:
                return "Unknown status";
        }
    }
}