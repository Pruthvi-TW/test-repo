package com.kyc.verification.repository;

import com.kyc.verification.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    @Query("SELECT a FROM AuditLog a WHERE a.ekycRequestId = :ekycRequestId ORDER BY a.timestamp DESC")
    List<AuditLog> findByEkycRequestIdOrderByTimestampDesc(@Param("ekycRequestId") String ekycRequestId);

    @Query("SELECT a FROM AuditLog a WHERE a.sessionId = :sessionId ORDER BY a.timestamp DESC")
    List<AuditLog> findBySessionIdOrderByTimestampDesc(@Param("sessionId") String sessionId);

    @Query("SELECT a FROM AuditLog a WHERE a.eventType = :eventType AND a.timestamp BETWEEN :startTime AND :endTime")
    Page<AuditLog> findByEventTypeAndTimeRange(
            @Param("eventType") String eventType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.ekycRequestId = :ekycRequestId AND a.eventType = :eventType")
    Optional<AuditLog> findByEkycRequestIdAndEventType(
            @Param("ekycRequestId") String ekycRequestId,
            @Param("eventType") String eventType);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.ekycRequestId = :ekycRequestId AND a.eventType = :eventType")
    long countByEkycRequestIdAndEventType(
            @Param("ekycRequestId") String ekycRequestId,
            @Param("eventType") String eventType);

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp < :retentionDate")
    List<AuditLog> findLogsOlderThan(@Param("retentionDate") LocalDateTime retentionDate);

    @Query("SELECT a FROM AuditLog a WHERE a.status = :status AND a.timestamp BETWEEN :startTime AND :endTime")
    Page<AuditLog> findByStatusAndTimeRange(
            @Param("status") String status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    @Query("SELECT DISTINCT a.ekycRequestId FROM AuditLog a WHERE a.eventType = :eventType AND a.status = :status")
    List<String> findDistinctEkycRequestIdsByEventTypeAndStatus(
            @Param("eventType") String eventType,
            @Param("status") String status);

    @Query("SELECT a FROM AuditLog a WHERE a.ekycRequestId = :ekycRequestId AND a.timestamp BETWEEN :startTime AND :endTime ORDER BY a.timestamp DESC")
    List<AuditLog> findByEkycRequestIdAndTimeRange(
            @Param("ekycRequestId") String ekycRequestId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("DELETE FROM AuditLog a WHERE a.timestamp < :retentionDate")
    void deleteLogsOlderThan(@Param("retentionDate") LocalDateTime retentionDate);

    @Query("SELECT COUNT(a) > 0 FROM AuditLog a WHERE a.ekycRequestId = :ekycRequestId AND a.eventType = :eventType AND a.status = :status")
    boolean existsByEkycRequestIdAndEventTypeAndStatus(
            @Param("ekycRequestId") String ekycRequestId,
            @Param("eventType") String eventType,
            @Param("status") String status);

    @Query("SELECT DISTINCT a.sessionId FROM AuditLog a WHERE a.timestamp BETWEEN :startTime AND :endTime")
    List<String> findDistinctSessionIdsInTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query(value = "SELECT * FROM audit_log WHERE " +
            "to_tsvector('english', event_description || ' ' || status || ' ' || event_type) @@ to_tsquery('english', :searchTerm)",
            nativeQuery = true)
    Page<AuditLog> fullTextSearch(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.ekycRequestId IN " +
            "(SELECT DISTINCT al.ekycRequestId FROM AuditLog al WHERE al.eventType = :eventType AND al.status = :status) " +
            "ORDER BY a.timestamp DESC")
    List<AuditLog> findAllLogsForRequestsWithEventTypeAndStatus(
            @Param("eventType") String eventType,
            @Param("status") String status);

    @Query("SELECT COUNT(DISTINCT a.ekycRequestId) FROM AuditLog a WHERE " +
            "a.timestamp BETWEEN :startTime AND :endTime AND a.status = :status")
    long countDistinctRequestsByStatusInTimeRange(
            @Param("status") String status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}