package com.kyc.verification.repository;

import com.kyc.verification.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    @Query("SELECT ov FROM OtpVerification ov WHERE ov.ekycRequest.id = :ekycRequestId AND ov.status = 'VERIFIED'")
    Optional<OtpVerification> findVerifiedOtpByEkycRequestId(@Param("ekycRequestId") Long ekycRequestId);

    @Query("SELECT ov FROM OtpVerification ov WHERE ov.ekycRequest.id = :ekycRequestId ORDER BY ov.createdAt DESC")
    List<OtpVerification> findAllByEkycRequestIdOrderByCreatedAtDesc(@Param("ekycRequestId") Long ekycRequestId);

    @Query("SELECT COUNT(ov) FROM OtpVerification ov WHERE ov.ekycRequest.id = :ekycRequestId AND ov.status = 'FAILED' AND ov.createdAt >= :since")
    int countFailedAttemptsInTimeWindow(@Param("ekycRequestId") Long ekycRequestId, @Param("since") LocalDateTime since);

    @Query("SELECT ov FROM OtpVerification ov WHERE ov.ekycRequest.referenceNumber = :referenceNumber AND ov.status = 'IN_PROGRESS' ORDER BY ov.createdAt DESC")
    Optional<OtpVerification> findActiveOtpByReferenceNumber(@Param("referenceNumber") String referenceNumber);

    @Query("SELECT ov FROM OtpVerification ov WHERE ov.ekycRequest.referenceNumber = :referenceNumber AND ov.otpHash = :otpHash AND ov.status = 'IN_PROGRESS'")
    Optional<OtpVerification> findByReferenceNumberAndOtpHash(@Param("referenceNumber") String referenceNumber, @Param("otpHash") String otpHash);

    @Query("SELECT ov FROM OtpVerification ov WHERE ov.createdAt < :retentionDate")
    List<OtpVerification> findOtpVerificationsOlderThan(@Param("retentionDate") LocalDateTime retentionDate);

    @Query("SELECT COUNT(ov) FROM OtpVerification ov WHERE ov.ekycRequest.id = :ekycRequestId AND ov.createdAt >= :since")
    int countTotalAttemptsInTimeWindow(@Param("ekycRequestId") Long ekycRequestId, @Param("since") LocalDateTime since);

    @Query("SELECT ov FROM OtpVerification ov WHERE ov.ekycRequest.sessionId = :sessionId ORDER BY ov.createdAt DESC")
    List<OtpVerification> findAllBySessionIdOrderByCreatedAtDesc(@Param("sessionId") String sessionId);

    @Query("SELECT CASE WHEN COUNT(ov) > 0 THEN true ELSE false END FROM OtpVerification ov " +
            "WHERE ov.ekycRequest.id = :ekycRequestId AND ov.status = 'IN_PROGRESS' AND ov.createdAt >= :cutoffTime")
    boolean hasActiveOtpInTimeWindow(@Param("ekycRequestId") Long ekycRequestId, @Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT ov FROM OtpVerification ov " +
            "WHERE ov.status = 'IN_PROGRESS' AND ov.createdAt <= :expirationTime")
    List<OtpVerification> findExpiredOtpVerifications(@Param("expirationTime") LocalDateTime expirationTime);

    @Query("SELECT COUNT(ov) FROM OtpVerification ov " +
            "WHERE ov.ekycRequest.id = :ekycRequestId AND ov.status = 'VERIFIED'")
    int countSuccessfulVerifications(@Param("ekycRequestId") Long ekycRequestId);

    @Query(value = "SELECT * FROM otp_verification ov " +
            "WHERE ov.ekyc_request_id = :ekycRequestId " +
            "AND ov.created_at >= :startTime " +
            "AND ov.created_at <= :endTime " +
            "ORDER BY ov.created_at DESC", 
            nativeQuery = true)
    List<OtpVerification> findVerificationHistoryInTimeRange(
            @Param("ekycRequestId") Long ekycRequestId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("DELETE FROM OtpVerification ov WHERE ov.ekycRequest.id = :ekycRequestId")
    void deleteAllByEkycRequestId(@Param("ekycRequestId") Long ekycRequestId);

    @Query("SELECT COUNT(ov) > 0 FROM OtpVerification ov " +
            "WHERE ov.ekycRequest.id = :ekycRequestId " +
            "AND ov.status = 'VERIFIED' " +
            "AND ov.createdAt >= :since")
    boolean hasSuccessfulVerificationSince(@Param("ekycRequestId") Long ekycRequestId, @Param("since") LocalDateTime since);
}