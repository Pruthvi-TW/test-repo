package com.kyc.verification.repository;

import com.kyc.verification.entity.VerificationConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationConsentRepository extends JpaRepository<VerificationConsent, Long>, JpaSpecificationExecutor<VerificationConsent> {

    Optional<VerificationConsent> findByEkycRequestId(Long ekycRequestId);

    Optional<VerificationConsent> findByEkycRequestReferenceNumber(String referenceNumber);

    @Query("SELECT vc FROM VerificationConsent vc WHERE vc.ekycRequestId = :requestId AND vc.isActive = true")
    Optional<VerificationConsent> findActiveConsentByRequestId(@Param("requestId") Long requestId);

    @Query("SELECT vc FROM VerificationConsent vc WHERE vc.consentTimestamp <= :expiryDate AND vc.isActive = true")
    List<VerificationConsent> findExpiredConsents(@Param("expiryDate") LocalDateTime expiryDate);

    @Query("SELECT COUNT(vc) > 0 FROM VerificationConsent vc WHERE vc.ekycRequestId = :requestId AND vc.identityVerificationConsent = true AND vc.isActive = true")
    boolean hasValidIdentityVerificationConsent(@Param("requestId") Long requestId);

    @Query("SELECT COUNT(vc) > 0 FROM VerificationConsent vc WHERE vc.ekycRequestId = :requestId AND vc.contactVerificationConsent = true AND vc.isActive = true")
    boolean hasValidContactVerificationConsent(@Param("requestId") Long requestId);

    @Modifying
    @Transactional
    @Query("UPDATE VerificationConsent vc SET vc.isActive = false, vc.deactivationTimestamp = :deactivationTime WHERE vc.ekycRequestId = :requestId")
    int deactivateConsent(@Param("requestId") Long requestId, @Param("deactivationTime") LocalDateTime deactivationTime);

    @Query("SELECT vc FROM VerificationConsent vc WHERE vc.consentTimestamp BETWEEN :startDate AND :endDate")
    List<VerificationConsent> findConsentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(vc) FROM VerificationConsent vc WHERE vc.ekycRequestId = :requestId AND vc.isActive = true AND vc.consentTimestamp > :timestamp")
    long countActiveConsentsAfterTimestamp(@Param("requestId") Long requestId, @Param("timestamp") LocalDateTime timestamp);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM verification_consent WHERE consent_timestamp < :retentionDate", nativeQuery = true)
    int deleteExpiredConsents(@Param("retentionDate") LocalDateTime retentionDate);

    @Query("SELECT vc FROM VerificationConsent vc WHERE vc.sessionId = :sessionId")
    List<VerificationConsent> findBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT vc FROM VerificationConsent vc WHERE vc.parentProcessId = :parentProcessId")
    List<VerificationConsent> findByParentProcessId(@Param("parentProcessId") String parentProcessId);

    @Modifying
    @Transactional
    @Query("UPDATE VerificationConsent vc SET vc.auditRemarks = :remarks WHERE vc.id = :consentId")
    int updateAuditRemarks(@Param("consentId") Long consentId, @Param("remarks") String remarks);

    @Query(value = "SELECT * FROM verification_consent vc WHERE vc.ekycrequest_id = :requestId FOR UPDATE", nativeQuery = true)
    Optional<VerificationConsent> findForUpdate(@Param("requestId") Long requestId);
}