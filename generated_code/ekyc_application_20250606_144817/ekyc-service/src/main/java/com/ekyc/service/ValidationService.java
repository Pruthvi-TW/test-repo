package com.ekyc.service;

import com.ekyc.exception.ValidationException;
import com.ekyc.model.EkycRequest;
import com.ekyc.model.OtpVerificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service responsible for validating input data for the eKYC process.
 * Ensures all data meets the required format and business rules.
 */
@Service
public class ValidationService {
    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);
    
    private final AuditService auditService;
    
    // Validation patterns
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^[0-9]{12}$");
    private static final Pattern VID_PATTERN = Pattern.compile("^[0-9]{16}$");
    private static final Pattern OTP_PATTERN = Pattern.compile("^[0-9]{6}$");
    private static final Pattern SESSION_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{1,50}$");
    
    @Autowired
    public ValidationService(AuditService auditService) {
        this.auditService = auditService;
    }
    
    /**
     * Validates an eKYC request.
     * 
     * @param request The eKYC request to validate
     * @throws ValidationException if the request is invalid
     */
    public void validateEkycRequest(EkycRequest request) {
        List<String> validationErrors = new ArrayList<>();
        
        // Check for null request
        if (request == null) {
            throw new ValidationException("Request cannot be null");
        }
        
        // Validate ID number
        if (request.getIdNumber() == null || request.getIdNumber().trim().isEmpty()) {
            validationErrors.add("ID number cannot be empty");
        } else {
            String idType = request.getIdType();
            if ("AADHAAR".equals(idType)) {
                if (!AADHAAR_PATTERN.matcher(request.getIdNumber()).matches()) {
                    validationErrors.add("Aadhaar number must be exactly 12 numeric digits");
                }
            } else if ("VID".equals(idType)) {
                if (!VID_PATTERN.matcher(request.getIdNumber()).matches()) {
                    validationErrors.add("VID must be exactly 16 numeric digits");
                }
            } else {
                validationErrors.add("ID type must be either 'AADHAAR' or 'VID'");
            }
        }
        
        // Validate ID type
        if (request.getIdType() == null || request.getIdType().trim().isEmpty()) {
            validationErrors.add("ID type cannot be empty");
        } else if (!("AADHAAR".equals(request.getIdType()) || "VID".equals(request.getIdType()))) {
            validationErrors.add("ID type must be either 'AADHAAR' or 'VID'");
        }
        
        // Validate identity verification consent
        if (!request.isIdentityVerificationConsent()) {
            validationErrors.add("Identity verification consent is mandatory");
        }
        
        // Validate session ID
        if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
            validationErrors.add("Session ID cannot be empty");
        } else if (!SESSION_ID_PATTERN.matcher(request.getSessionId()).matches()) {
            validationErrors.add("Session ID contains invalid characters or exceeds maximum length");
        }
        
        // If there are validation errors, throw an exception
        if (!validationErrors.isEmpty()) {
            String errorMessage = String.join("; ", validationErrors);
            logger.warn("eKYC request validation failed: {}", errorMessage);
            
            String maskedId = request.getIdNumber() != null ? 
                    auditService.maskAadhaarOrVid(request.getIdNumber()) : "null";
            
            auditService.logFailure("VALIDATION", "N/A", maskedId, 
                    "eKYC request validation failed: " + errorMessage);
            
            throw new ValidationException(errorMessage);
        }
        
        logger.info("eKYC request validation successful");
    }
    
    /**
     * Validates an OTP verification request.
     * 
     * @param request The OTP verification request to validate
     * @throws ValidationException if the request is invalid
     */
    public void validateOtpRequest(OtpVerificationRequest request) {
        List<String> validationErrors = new ArrayList<>();
        
        // Check for null request
        if (request == null) {
            throw new ValidationException("OTP request cannot be null");
        }
        
        // Validate OTP
        if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
            validationErrors.add("OTP cannot be empty");
        } else if (!OTP_PATTERN.matcher(request.getOtp()).matches()) {
            validationErrors.add("OTP must be exactly 6 numeric digits");
        }
        
        // Validate reference number
        if (request.getReferenceNumber() == null || request.getReferenceNumber().