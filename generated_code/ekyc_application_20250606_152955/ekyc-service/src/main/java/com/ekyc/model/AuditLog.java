package com.ekyc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an audit log entry for tracking system activities.
 * This entity stores detailed information about system events, user actions,
 * and data changes for audit and compliance purposes.
 *
 * @author eKYC Team
 * @version 1.0.0
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_log_event_type", columnList = "event_type"),
        @Index(name = "idx_audit_log_entity_type", columnList = "entity_type"),
        @Index(name = "idx_audit_log_entity_id", columnList = "entity_id"),
        @Index(name = "idx_audit_log_user_id", columnList = "user_id"),
        @Index(name = "idx_audit_log_timestamp", columnList = "timestamp"),
        @Index(name = "idx_audit_log_session_id", columnList = "session_id")
})
public class AuditLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Enum representing the type of event being audited.
     */
    public enum EventType {
        CREATE, READ, UPDATE, DELETE, LOGIN, LOGOUT, API_CALL, VERIFICATION, ERROR
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 20, nullable = false)
    private EventType eventType;

    @NotBlank
    @Size(max = 100)
    @Column(name = "entity_type", length = 100, nullable = false)
    private String entityType;

    @Column(name = "entity_id", length = 100)
    private String entityId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "user_id", length = 100, nullable = false)
    private String userId;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "action", length = 200)
    private String action;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "details", length = 4000)
    private String details;

    @Column(name = "old_value", length = 4000)
    private String oldValue;

    @Column(name = "new_value", length = 4000)
    private String newValue;

    /**
     * Default constructor.
     */
    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Gets the type of event being audited.
     *
     * @return The event type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Sets the type of event being audited.
     *
     * @param eventType The event type to set
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the type of entity being audited.
     *
     * @return The entity type
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Sets the type of entity being audited.
     *
     * @param entityType The entity type to set
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * Gets the ID of the entity being audited.
     *
     * @return The entity ID
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * Sets the ID of the entity being audited.
     *
     * @param entityId The entity ID to set
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets the ID of the user who performed the action.
     *
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who performed the action.
     *
     * @param userId The user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the timestamp when the event occurred.
     *
     * @return The event timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when the event occurred.
     *
     * @param timestamp The event timestamp to set
     */
    public void setTimestamp(Local