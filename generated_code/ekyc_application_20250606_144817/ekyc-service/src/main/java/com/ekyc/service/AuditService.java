package com.ekyc.service;

import com.ekyc.model.AuditLog;
import com.ekyc.model.AuditLogType;
import com.ekyc.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Service responsible for audit logging with PII masking.
 * Ensures all sensitive data is properly masked before logging.
 */
@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    private final AuditLogRepository auditLogRepository;
    
    // Patterns for identifying sensitive data
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("\\d{12}");
    private static final Pattern VID_PATTERN = Pattern.compile("\\d{16}");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("\\d{10}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    
    @Autowired
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    /**
     * Logs a successful operation.
     * 
     * @param action The action being performed
     * @param referenceId The reference ID for the operation
     * @param maskedData The already masked sensitive data
     * @param message The log message
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSuccess(String action, String referenceId, String maskedData, String message) {
        createAuditLog(AuditLogType.SUCCESS, action, referenceId, maskedData, message);
        logger.info("[SUCCESS][{}][{}] {}", action, referenceId, message);
    }
    
    /**
     * Logs a failed operation.
     * 
     * @param action The action being performed
     * @param referenceId The reference ID for the operation
     * @param maskedData The already masked sensitive data
     * @param message The log message
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(String action, String referenceId, String maskedData, String message) {
        createAuditLog(AuditLogType.FAILURE, action, referenceId, maskedData, message);
        logger.error("[FAILURE][{}][{}] {}", action, referenceId, message);
    }
    
    /**
     * Logs a warning.
     * 
     * @param action The action being performed
     * @param referenceId The reference ID for the operation
     * @param maskedData The already masked sensitive data
     * @param message The log message
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logWarning(String action, String referenceId, String maskedData, String message) {
        createAuditLog(AuditLogType.WARNING, action, referenceId, maskedData, message);
        logger.warn("[WARNING][{}][{}] {}", action, referenceId, message);
    }
    
    /**
     * Logs an API call.
     * 
     * @param action The API being called
     * @param referenceId The reference ID for the operation
     * @param maskedData The already masked sensitive data
     * @param message The log message
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logApiCall(String action, String referenceId, String maskedData, String message) {
        createAuditLog(AuditLogType.API_CALL, action, referenceId, maskedData, message);
        logger.info("[API_CALL][{}][{}] {}", action, referenceId, message);
    }
    
    /**
     * Creates and persists an audit log entry.
     * 
     * @param type The type of audit log
     * @param action The action being performed
     * @param referenceId The reference ID for the operation
     * @param maskedData The already masked sensitive data
     * @param message The log message
     */
    private void createAuditLog(AuditLogType type, String action, String referenceId, 
                               String maskedData, String message) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setId(UUID.randomUUID().toString());
            auditLog.setType(type);
            auditLog.setAction(action);
            auditLog.setReferenceId(referenceId);
            auditLog.setMaskedData(maskedData);
            auditLog.setMessage(maskSensitiveData(message));
            auditLog.setTimestamp(LocalDateTime.now());
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Fallback to console logging if database logging fails
            logger.error("Failed to save audit log: {}", e.getMessage());
            logger.info("[FALLBACK_LOG][{}][{}][{}] {}", type, action, referenceId, message);
        }
    }
    
    /**
     * Masks an Aadhaar or VID number for secure logging.
     * 
     * @param idNumber The Aadhaar or VID number to mask
     * @return The masked ID number
     */
    public String maskAadhaarOrVid(String idNumber) {
        if (idNumber == null) {
            return "null";
        }
        
        if (idNumber.length() == 12) {
            // Aadhaar number (12 digits)
            return "XXXX-XXXX-" + idNumber.substring(8);
        } else if (idNumber.length() == 16) {
            // VID (16 digits)
            return "XXXX-XXXX-XXXX-" + idNumber.substring(12);
        } else {
            // Unknown format, mask everything
            return "MASKED-ID";
        }
    }
    
    /**
     * Masks a mobile number for secure logging.
     * 
     * @param mobileNumber The mobile number to mask
     * @return The masked mobile number
     */
    public String maskMobileNumber(String mobileNumber) {
        if (mobileNumber == null) {
            return "null";
        }
        
        if (mobileNumber.length() == 10) {
            return "XXXXXX" + mobileNumber.substring(6);
        } else {
            return "MASKED-MOBILE";
        }
    }
    
    /**
     * Masks an email address for secure logging.
     * 
     * @param email The email address to mask
     * @return The masked email address
     */
    public String maskEmail(String email) {
        if (email == null) {
            return "null";
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex > 1) {
            String username = email.substring(0, atIndex);
            String domain = email.substring(atIndex);
            
            if (username.length() > 2) {
                return username.substring(0, 1) + "..." + username.substring(username.length() - 1) + domain;
            } else {
                return "x" + domain;
            }
        } else {
            return "MASKED-EMAIL";
        }
    }
    
    /**
     * Masks all sensitive data in a string.
     * 
     * @param input The input string that may contain sensitive data
     * @return The input with all sensitive data masked
     */
    public String maskSensitiveData(String input) {
        if (input == null) {
            return null;
        }
        
        // Mask Aadhaar numbers
        input = AADHAAR_PATTERN.matcher(input).replaceAll(matchResult -> {
            String match = matchResult.group();
            return "XXXX-XXXX-" + match.substring(8);
        });
        
        // Mask VIDs
        input = VID_PATTERN.matcher(input).replaceAll(matchResult -> {
            String match = matchResult.group();
            return "XXXX-XXXX-XXXX-" + match.substring(12);
        });
        
        // Mask mobile numbers
        input = MOBILE_PATTERN.matcher(input).replaceAll(matchResult -> {
            String match = matchResult.group();
            return "XXXXXX" + match.substring(6);
        });
        
        // Mask email addresses
        input = EMAIL_PATTERN.matcher(input).replaceAll(matchResult -> {
            String match = matchResult.group();
            int atIndex = match.indexOf('@');
            if (atIndex > 1) {
                String username = match.substring(0, atIndex);
                String domain = match.substring(atIndex);
                
                if (username.length() > 2) {
                    return username.substring(0, 1) + "..." + username.substring(username.length() - 1) + domain;
                } else {
                    return "x" + domain;
                }
            } else {
                return "MASKED-EMAIL";
            }
        });
        
        return input;
    }
    
    /**
     * Retrieves audit logs for a specific reference ID.
     * 
     * @param referenceId The reference ID to search for
     * @return A list of audit logs for the reference ID
     */
    @Transactional(readOnly = true)
    public Iterable<AuditLog> getAuditLogsForReference(String referenceId) {
        return auditLogRepository.findByReferenceIdOrderByTimestampDesc(referenceId);
    }
}