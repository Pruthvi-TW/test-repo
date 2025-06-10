package com.ekyc.service;

import com.ekyc.model.AuditLog;
import com.ekyc.model.AuditLogType;
import com.ekyc.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsible for audit logging and PII data masking.
 * Ensures all sensitive data is properly masked before logging.
 */
@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    private final AuditLogRepository auditLogRepository;
    
    @Autowired
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    /**
     * Logs an informational message to the audit log.
     * 
     * @param message The message to log
     * @param sessionId The session ID, if available
     * @param referenceNumber The reference number, if available
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logInfo(String message, String sessionId, String referenceNumber) {
        createAuditLog(message, AuditLogType.INFO, sessionId, referenceNumber, null);
        logger.info("{} - Session: {}, Reference: {}", 
                message, 
                sessionId != null ? sessionId : "N/A", 
                referenceNumber != null ? referenceNumber : "N/A");
    }
    
    /**
     * Logs a success message to the audit log.
     * 
     * @param message The message to log
     * @param sessionId The session ID, if available
     * @param referenceNumber The reference number, if available
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSuccess(String message, String sessionId, String referenceNumber) {
        createAuditLog(message, AuditLogType.SUCCESS, sessionId, referenceNumber, null);
        logger.info("{} - Session: {}, Reference: {}", 
                message, 
                sessionId != null ? sessionId : "N/A", 
                referenceNumber != null ? referenceNumber : "N/A");
    }
    
    /**
     * Logs a failure message to the audit log.
     * 
     * @param message The message to log
     * @param sessionId The session ID, if available
     * @param referenceNumber The reference number, if available
     * @param errorDetails Additional error details, if available
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(String message, String sessionId, String referenceNumber, String errorDetails) {
        createAuditLog(message, AuditLogType.FAILURE, sessionId, referenceNumber, errorDetails);
        logger.error("{} - Session: {}, Reference: {}, Error: {}", 
                message, 
                sessionId != null ? sessionId : "N/A", 
                referenceNumber != null ? referenceNumber : "N/A", 
                errorDetails != null ? errorDetails : "N/A");
    }
    
    /**
     * Creates and persists an audit log entry.
     * 
     * @param message The message to log
     * @param logType The type of log (INFO, SUCCESS, FAILURE)
     * @param sessionId The session ID, if available
     * @param referenceNumber The reference number, if available
     * @param errorDetails Additional error details, if available
     */
    private void createAuditLog(String message, AuditLogType logType, 
                               String sessionId, String referenceNumber, String errorDetails) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setMessage(message);
            auditLog.setLogType(logType);
            auditLog.setSessionId(sessionId);
            auditLog.setReferenceNumber(referenceNumber);
            auditLog.setErrorDetails(errorDetails);
            auditLog.setTimestamp(LocalDateTime.now());
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Log to console if database logging fails
            logger.error("Failed to write to audit log database: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Masks an Aadhaar or VID number for secure logging.
     * 
     * @param idNumber The Aadhaar or VID number to mask
     * @return The masked ID number
     */
    public String maskAadhaarOrVid(String idNumber) {
        if (idNumber == null || idNumber.length() < 4) {
            return "****";
        }
        
        int visibleDigits = 4;
        int maskLength = idNumber.length() - visibleDigits;
        
        return "X".repeat(maskLength) + idNumber.substring(maskLength);
    }
    
    /**
     * Masks an OTP for secure logging.
     * 
     * @param otp The OTP to mask
     * @return The masked OTP
     */
    public String maskOtp(String otp) {
        if (otp == null) {
            return "****";
        }
        
        return "*".repeat(otp.length());
    }
    
    /**
     * Masks a mobile number for secure logging.
     * 
     * @param mobileNumber The mobile number to mask
     * @return The masked mobile number
     */
    public String maskMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.length() < 4) {
            return "****";
        }
        
        int visibleDigits = 4;
        int maskLength = mobileNumber.length() - visibleDigits;
        
        return "X".repeat(maskLength) + mobileNumber.substring(maskLength);
    }
    
    /**
     * Masks an email address for secure logging.
     * 
     * @param email The email address to mask
     * @return The masked email address
     */
    public String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "****";
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "****" + (atIndex >= 0 ? email.substring(atIndex) : "");
        }
        
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        String maskedUsername;
        if (username.length() <= 2) {
            maskedUsername = username.charAt(0) + "***";
        } else {
            maskedUsername = username.charAt(0) + 
                    "*".repeat(username.length() - 2) + 
                    username.charAt(username.length() - 1);
        }
        
        return maskedUsername + domain;
    }
}