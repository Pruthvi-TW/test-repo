package com.kyc.verification.repository;

import com.kyc.verification.entity.EkycRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EkycRequestRepository extends JpaRepository<EkycRequest, Long> {

    @Query("SELECT e FROM EkycRequest e WHERE e.referenceNumber = :referenceNumber")
    Optional<EkycRequest> findByReferenceNumber(@Param("referenceNumber") String referenceNumber);

    @Query("SELECT e FROM EkycRequest e WHERE e.sessionId = :sessionId")
    Optional<EkycRequest> findBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT e FROM EkycRequest e WHERE e.status = :status")
    List<EkycRequest> findByStatus(@Param("status") String status);

    @Query("SELECT e FROM EkycRequest e WHERE e.status = :status AND e.createdAt <= :cutoffTime")
    List<EkycRequest> findExpiredRequests(
            @Param("status") String status,
            @Param("cutoffTime") LocalDateTime cutoffTime
    );

    @Query("SELECT e FROM EkycRequest e WHERE e.parentProcessId = :parentProcessId")
    List<EkycRequest> findByParentProcessId(@Param("parentProcessId") String parentProcessId);

    @Query("SELECT e FROM EkycRequest e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    Page<EkycRequest> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(e) FROM EkycRequest e WHERE e.status = :status AND e.createdAt >= :since")
    long countByStatusSince(
            @Param("status") String status,
            @Param("since") LocalDateTime since
    );

    @Modifying
    @Query("UPDATE EkycRequest e SET e.status = :newStatus WHERE e.referenceNumber = :referenceNumber")
    int updateStatus(
            @Param("referenceNumber") String referenceNumber,
            @Param("newStatus") String newStatus
    );

    @Query("SELECT e FROM EkycRequest e WHERE " +
            "e.status = 'IN_PROGRESS' AND " +
            "e.createdAt <= :timeoutThreshold AND " +
            "e.lastModifiedAt <= :timeoutThreshold")
    List<EkycRequest> findStaleRequests(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);

    @Query("SELECT DISTINCT e.aadhaarNumber FROM EkycRequest e WHERE " +
            "e.status = 'FAILED' AND " +
            "e.createdAt >= :since " +
            "GROUP BY e.aadhaarNumber " +
            "HAVING COUNT(e) >= :failureThreshold")
    List<String> findAadhaarNumbersWithExcessiveFailures(
            @Param("since") LocalDateTime since,
            @Param("failureThreshold") int failureThreshold
    );

    @Modifying
    @Query("DELETE FROM EkycRequest e WHERE e.status IN ('COMPLETED', 'FAILED') " +
            "AND e.createdAt < :retentionDate")
    int deleteExpiredRecords(@Param("retentionDate") LocalDateTime retentionDate);

    @Query("SELECT e FROM EkycRequest e " +
            "WHERE e.status = :status " +
            "AND e.lastModifiedAt < :cutoffTime " +
            "AND (SELECT COUNT(o) FROM OtpVerification o WHERE o.ekycRequest = e) >= :maxAttempts")
    List<EkycRequest> findRequestsExceedingOtpAttempts(
            @Param("status") String status,
            @Param("cutoffTime") LocalDateTime cutoffTime,
            @Param("maxAttempts") int maxAttempts
    );

    @Query("SELECT e FROM EkycRequest e JOIN FETCH e.verificationConsent vc " +
            "WHERE e.referenceNumber = :referenceNumber")
    Optional<EkycRequest> findByReferenceNumberWithConsent(@Param("referenceNumber") String referenceNumber);

    @Query("SELECT COUNT(e) > 0 FROM EkycRequest e " +
            "WHERE e.aadhaarNumber = :aadhaarNumber " +
            "AND e.status = 'VERIFIED' " +
            "AND e.createdAt >= :since")
    boolean hasSuccessfulVerificationSince(
            @Param("aadhaarNumber") String aadhaarNumber,
            @Param("since") LocalDateTime since
    );

    @Query(value = "SELECT * FROM ekyc_request e " +
            "WHERE e.status = :status " +
            "AND e.created_at >= :startDate " +
            "AND e.created_at < :endDate " +
            "ORDER BY e.created_at DESC " +
            "LIMIT :limit", 
            nativeQuery = true)
    List<EkycRequest> findRecentRequestsByStatus(
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("limit") int limit
    );
}