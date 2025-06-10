package com.kyc.verification.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "otp_verifications")
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ekyc_request_id", nullable = false, updatable = false)
    private EkycRequest ekycRequest;

    @NotBlank
    @Size(min = 6, max = 6)
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be exactly 6 digits")
    @Column(name = "otp_hash", nullable = false)
    private String otpHash;

    @NotNull
    @Column(name = "reference_id", nullable = false, updatable = false)
    private String referenceId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VerificationStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "response_hash")
    private String responseHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "session_id", nullable = false, updatable = false)
    private String sessionId;

    @Column(name = "trace_id", nullable = false, updatable = false)
    private String traceId;

    public OtpVerification() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public EkycRequest getEkycRequest() {
        return ekycRequest;
    }

    public void setEkycRequest(EkycRequest ekycRequest) {
        this.ekycRequest = ekycRequest;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public String getResponseHash() {
        return responseHash;
    }

    public void setResponseHash(String responseHash) {
        this.responseHash = responseHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtpVerification that = (OtpVerification) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(referenceId, that.referenceId) &&
                Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, referenceId, sessionId);
    }

    @Override
    public String toString() {
        return "OtpVerification{" +
                "id=" + id +
                ", referenceId='" + referenceId + '\'' +
                ", status=" + status +
                ", attemptNumber=" + attemptNumber +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", expiresAt=" + expiresAt +
                ", sessionId='" + sessionId + '\'' +
                ", traceId='" + traceId + '\'' +
                '}';
    }

    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        FAILED,
        EXPIRED
    }

    public static class Builder {
        private final OtpVerification otpVerification;

        public Builder() {
            otpVerification = new OtpVerification();
        }

        public Builder ekycRequest(EkycRequest ekycRequest) {
            otpVerification.setEkycRequest(ekycRequest);
            return this;
        }

        public Builder otpHash(String otpHash) {
            otpVerification.setOtpHash(otpHash);
            return this;
        }

        public Builder referenceId(String referenceId) {
            otpVerification.setReferenceId(referenceId);
            return this;
        }

        public Builder status(VerificationStatus status) {
            otpVerification.setStatus(status);
            return this;
        }

        public Builder failureReason(String failureReason) {
            otpVerification.setFailureReason(failureReason);
            return this;
        }

        public Builder attemptNumber(Integer attemptNumber) {
            otpVerification.setAttemptNumber(attemptNumber);
            return this;
        }

        public Builder responseHash(String responseHash) {
            otpVerification.setResponseHash(responseHash);
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            otpVerification.setExpiresAt(expiresAt);
            return this;
        }

        public Builder sessionId(String sessionId) {
            otpVerification.setSessionId(sessionId);
            return this;
        }

        public Builder traceId(String traceId) {
            otpVerification.setTraceId(traceId);
            return this;
        }

        public OtpVerification build() {
            return otpVerification;
        }
    }
}