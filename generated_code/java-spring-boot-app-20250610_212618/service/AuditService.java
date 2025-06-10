package com.kyc.verification.service;

import com.kyc.verification.entity.AuditLog;
import com.kyc.verification.entity.EkycRequest;
import com.kyc.verification.enums.AuditLogType;
import com.kyc.verification.enums.LogCategory;
import com.kyc.verification.repository.AuditLogRepository;
import com.kyc.verification.util.DataMaskingUtil;
import com.kyc.verification.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;
    private final DataMaskingUtil dataMaskingUtil;
    private final SecurityUtil securityUtil;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository,
                       DataMaskingUtil dataMaskingUtil,
                       SecurityUtil securityUtil) {
        this.auditLogRepository = auditLogRepository;
        this.dataMaskingUtil = dataMaskingUtil;
        this.securityUtil = securityUtil;
    }

    @Transactional
    public void logEvent(EkycRequest ekycRequest,
                        AuditLogType logType,
                        LogCategory category,
                        String event,
                        Map<String, Object> details) {
        try {
            String maskedDetails = dataMaskingUtil.maskSensitiveData(details);
            
            AuditLog auditLog = new AuditLog();
            auditLog.setAuditId(UUID.randomUUID().toString());
            auditLog.setEkycRequest(ekycRequest);
            auditLog.setLogType(logType);
            auditLog.setCategory(category);
            auditLog.setEvent(event);
            auditLog.setDetails(maskedDetails);
            auditLog.setTimestamp(LocalDateTime.now());
            auditLog.setUserId(securityUtil.getCurrentUserId());
            auditLog.setIpAddress(securityUtil.getCurrentUserIp());
            auditLog.setSessionId(securityUtil.getCurrentSessionId());
            auditLog.setReferenceNumber(ekycRequest.getReferenceNumber());

            auditLogRepository.save(auditLog);

            logger.info("Audit log created: type={}, category={}, event={}, referenceNumber={}",
                    logType, category, event, ekycRequest.getReferenceNumber());

        } catch (Exception e) {
            logger.error("Error creating audit log: type={}, category={}, event={}, error={}",
                    logType, category, event, e.getMessage(), e);
            throw new RuntimeException("Failed to create audit log", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(String referenceNumber, 
                                      LocalDateTime startDate,
                                      LocalDateTime endDate,
                                      LogCategory category,
                                      Pageable pageable) {
        try {
            return auditLogRepository.findByFilters(referenceNumber, startDate, endDate, category, pageable);
        } catch (Exception e) {
            logger.error("Error retrieving audit logs: referenceNumber={}, error={}", 
                    referenceNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve audit logs", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByEkycRequest(EkycRequest ekycRequest, Pageable pageable) {
        try {
            return auditLogRepository.findByEkycRequest(ekycRequest, pageable);
        } catch (Exception e) {
            logger.error("Error retrieving audit logs for eKYC request: referenceNumber={}, error={}", 
                    ekycRequest.getReferenceNumber(), e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve audit logs for eKYC request", e);
        }
    }

    @Transactional(readOnly = true)
    public long getAuditLogCount(String referenceNumber, 
                               LocalDateTime startDate,
                               LocalDateTime endDate,
                               LogCategory category) {
        try {
            return auditLogRepository.countByFilters(referenceNumber, startDate, endDate, category);
        } catch (Exception e) {
            logger.error("Error counting audit logs: referenceNumber={}, error={}", 
                    referenceNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to count audit logs", e);
        }
    }

    @Transactional
    public void deleteAuditLogsByRetentionPolicy(LocalDateTime retentionDate) {
        try {
            long deletedCount = auditLogRepository.deleteByTimestampBefore(retentionDate);
            logger.info("Deleted {} audit logs older than {}", deletedCount, retentionDate);
        } catch (Exception e) {
            logger.error("Error deleting audit logs by retention policy: date={}, error={}", 
                    retentionDate, e.getMessage(), e);
            throw new RuntimeException("Failed to delete audit logs by retention policy", e);
        }
    }

    @Transactional
    public void logSystemError(String referenceNumber,
                             String errorCode,
                             String errorMessage,
                             Throwable exception) {
        try {
            Map<String, Object> errorDetails = Map.of(
                "errorCode", errorCode,
                "errorMessage", errorMessage,
                "stackTrace", exception != null ? exception.getMessage() : "N/A"
            );

            AuditLog auditLog = new AuditLog();
            auditLog.setAuditId(UUID.randomUUID().toString());
            auditLog.setLogType(AuditLogType.ERROR);
            auditLog.setCategory(LogCategory.SYSTEM);
            auditLog.setEvent("SYSTEM_ERROR");
            auditLog.setDetails(dataMaskingUtil.maskSensitiveData(errorDetails));
            auditLog.setTimestamp(LocalDateTime.now());
            auditLog.setReferenceNumber(referenceNumber);

            auditLogRepository.save(auditLog);

            logger.error("System error logged: referenceNumber={}, errorCode={}, errorMessage={}",
                    referenceNumber, errorCode, errorMessage, exception);

        } catch (Exception e) {
            logger.error("Error logging system error: referenceNumber={}, error={}", 
                    referenceNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to log system error", e);
        }
    }

    @Transactional
    public void logSecurityEvent(String referenceNumber,
                               String event,
                               Map<String, Object> details) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setAuditId(UUID.randomUUID().toString());
            auditLog.setLogType(AuditLogType.SECURITY);
            auditLog.setCategory(LogCategory.SECURITY);
            auditLog.setEvent(event);
            auditLog.setDetails(dataMaskingUtil.maskSensitiveData(details));
            auditLog.setTimestamp(LocalDateTime.now());
            auditLog.setReferenceNumber(referenceNumber);
            auditLog.setUserId(securityUtil.getCurrentUserId());
            auditLog.setIpAddress(securityUtil.getCurrentUserIp());
            auditLog.setSessionId(securityUtil.getCurrentSessionId());

            auditLogRepository.save(auditLog);

            logger.info("Security event logged: referenceNumber={}, event={}", referenceNumber, event);

        } catch (Exception e) {
            logger.error("Error logging security event: referenceNumber={}, event={}, error={}", 
                    referenceNumber, event, e.getMessage(), e);
            throw new RuntimeException("Failed to log security event", e);
        }
    }
}