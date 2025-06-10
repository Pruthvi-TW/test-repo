package com.kyc.verification.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_log_session_id", columnList = "sessionId"),
    @Index(name = "idx_audit_log_ekyc_request_id", columnList = "ekycRequestId"),
    @Index(name = "idx_audit_log_event_type", columnList = "eventType"),
    @Index(name = "idx_audit_log_created_at", columnList = "createdAt")
})
public class AuditLog {

    @Id
    @UuidGenerator
    private String id;

    @NotNull
    @Column(nullable = false)
    private String sessionId;

    @Column(name = "ekyc_request_id")
    private String ekycRequestId;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditEventType eventType;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String eventDescription;

    @Column(columnDefinition = "TEXT")
    private String maskedRequestData;

    @Column(columnDefinition = "TEXT")
    private String maskedResponseData;

    @NotNull
    @Column(nullable = false)
    private String performedBy;

    @Column(name = "source_ip")
    private String sourceIp;

    @Column(name = "user_agent")
    private String userAgent;

    @NotNull
    @Column(nullable = false)
    private String status;

    @Size(max = 500)
    private String errorDetails;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    public enum AuditEventType {
        EKYC_REQUEST_INITIATED,
        OTP_GENERATED,
        OTP_VERIFICATION_ATTEMPT,
        EKYC_VERIFICATION_COMPLETED,
        CONSENT_RECORDED,
        API_INTEGRATION_CALL,
        SYSTEM_ERROR,
        DATA_MODIFICATION,
        SECURITY_EVENT,
        CONFIGURATION_CHANGE
    }

    // Default constructor for JPA
    protected AuditLog() {}

    // Builder constructor
    private AuditLog(Builder builder) {
        this.sessionId = builder.sessionId;
        this.ekycRequestId = builder.ekycRequestId;
        this.eventType = builder.eventType;
        this.eventDescription = builder.eventDescription;
        this.maskedRequestData = builder.maskedRequestData;
        this.maskedResponseData = builder.maskedResponseData;
        this.performedBy = builder.performedBy;
        this.sourceIp = builder.sourceIp;
        this.userAgent = builder.userAgent;
        this.status = builder.status;
        this.errorDetails = builder.errorDetails;
    }

    // Builder pattern implementation
    public static class Builder {
        private String sessionId;
        private String ekycRequestId;
        private AuditEventType eventType;
        private String eventDescription;
        private String maskedRequestData;
        private String maskedResponseData;
        private String performedBy;
        private String sourceIp;
        private String userAgent;
        private String status;
        private String errorDetails;

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder ekycRequestId(String ekycRequestId) {
            this.ekycRequestId = ekycRequestId;
            return this;
        }

        public Builder eventType(AuditEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder eventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
            return this;
        }

        public Builder maskedRequestData(String maskedRequestData) {
            this.maskedRequestData = maskedRequestData;
            return this;
        }

        public Builder maskedResponseData(String maskedResponseData) {
            this.maskedResponseData = maskedResponseData;
            return this;
        }

        public Builder performedBy(String performedBy) {
            this.performedBy = performedBy;
            return this;
        }

        public Builder sourceIp(String sourceIp) {
            this.sourceIp = sourceIp;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder errorDetails(String errorDetails) {
            this.errorDetails = errorDetails;
            return this;
        }

        public AuditLog build() {
            return new AuditLog(this);
        }
    }

    // Getters - All fields are immutable after creation
    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getEkycRequestId() {
        return ekycRequestId;
    }

    public AuditEventType getEventType() {
        return eventType;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getMaskedRequestData() {
        return maskedRequestData;
    }

    public String getMaskedResponseData() {
        return maskedResponseData;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(id, auditLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id='" + id + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", ekycRequestId='" + ekycRequestId + '\'' +
                ", eventType=" + eventType +
                ", eventDescription='" + eventDescription + '\'' +
                ", performedBy='" + performedBy + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}