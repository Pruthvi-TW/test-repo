package com.ekyc.service;

import com.ekyc.exception.ValidationException;
import com.ekyc.model.EkycRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Service responsible for validating eKYC requests and related data.
 * Ensures all input data meets the required format and business rules.
 */
@Service
public class ValidationService {
    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);
    
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^[0-9]{12}$");
    private static final Pattern OTP_PATTERN = Pattern.compile("^[0-9]{6}$");
    
    private final AuditService auditService;
    
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
        logger.debug("Validating eKYC request");
        
        if (request == null) {
            throw new ValidationException("Request cannot be null");
        }
        
        // Validate ID number
        validateIdNumber(request.getIdNumber());
        
        // Validate ID type
        if (request.getIdType() == null) {
            throw new ValidationException("ID type is required");
        }
        
        // Validate session ID
        if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
            throw new ValidationException("Session ID is required");
        }
        
        // Validate consent
        if (!request.isIdentityVerificationConsent()) {
            throw new ValidationException("Identity verification consent is required");
        }
        
        logger.debug("eKYC request validation successful");
    }
    
    /**
     * Validates an Aadhaar or VID number.
     * 
     * @param idNumber The ID number to validate
     * @throws ValidationException if the ID number is invalid
     */
    public void validateIdNumber(String idNumber) {
        if (idNumber == null || idNumber.trim().isEmpty()) {
            throw new ValidationException("ID number is required");
        }
        
        if (!AADHAAR_PATTERN.matcher(idNumber).matches()) {
            throw new ValidationException("ID number must be exactly 12 numeric digits");
        }
    }
    
    /**
     * Validates an OTP.
     * 
     * @param otp The OTP to validate
     * @throws ValidationException if the OTP is invalid
     */
    public void validateOtp(String otp) {
        if (otp == null || otp.trim().isEmpty()) {
            throw new ValidationException("OTP is required");
        }
        
        if (!OTP_PATTERN.matcher(otp).matches()) {
            throw new ValidationException("OTP must be exactly 6 numeric digits");
        }
    }
    
    /**
     * Validates a reference number.
     * 
     * @param referenceNumber The reference number to validate
     * @throws ValidationException if the reference number is invalid
     */
    public void validateReferenceNumber(String referenceNumber) {
        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            throw new ValidationException("Reference number is required");
        }
        
        if (!referenceNumber.startsWith("EKYC-") || referenceNumber.length() < 6) {
            throw new ValidationException("Invalid reference number format");
        }
    }
    
    /**
     * Validates a mobile number.
     * 
     * @param mobileNumber The mobile number to validate
     * @throws ValidationException if the mobile number is invalid
     */
    public void validateMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            throw new ValidationException("Mobile number is required");
        }
        
        if (!mobileNumber.matches("^[0-9]{10}$")) {
            throw new ValidationException("Mobile number must be exactly 10 numeric digits");
        }
    }
    
    /**
     * Validates an email address.
     * 
     * @param email The email address to validate
     * @throws ValidationException if the email address is invalid
     */
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            throw new ValidationException("Invalid email format");
        }
    }
}