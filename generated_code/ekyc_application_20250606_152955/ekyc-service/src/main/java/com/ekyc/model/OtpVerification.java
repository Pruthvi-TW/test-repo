package com.ekyc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an OTP verification attempt for an eKYC request.
 * This entity stores the details of an OTP verification attempt, including
 * the OTP code (hashed), verification status, and related timestamps.
 *
 * @author eKYC Team
 * @version 1.0.0
 */
@Entity
@Table(name = "otp_verifications", indexes = {
        @Index(name = "idx_otp_verification_reference_number", columnList = "reference_number"),
        @Index(name = "idx_otp_verification_ekyc_request_id", columnList = "ekyc_request_id"),
        @Index(name = "idx_otp_verification_status", columnList = "status"),
        @Index(name = "idx_otp_verification_created_at", columnList = "created_at")
})
public class OtpVerification extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Enum representing the status of the OTP verification attempt.
     */
    public enum VerificationStatus {
        INITIATED, VERIFIED, FAILED, EXPIRED
    }

    /**
     * Enum representing the reason for OTP verification failure.
     */
    public enum FailureReason {
        INVALID_OTP, EXPIRED_OTP, EXCEEDED_ATTEMPTS, TECHNICAL_ERROR
    }

    @NotBlank
    @Column(name = "reference_number", length = 36, nullable = false, unique = true)
    private String referenceNumber;

    @Column(name = "otp_hash", length = 255)
    private String otpHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private VerificationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_reason", length = 30)
    private FailureReason failureReason;

    @Column(name = "verification_completed_at")
    private LocalDateTime verificationCompletedAt;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "attempt_count")
    private Integer attemptCount;

    @Column(name = "external_transaction_id", length = 100)
    private String externalTransactionId;

    @Column(name = "response_hash", length = 255)
    private String responseHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ekyc_request_id", nullable = false)
    private EkycRequest ekycRequest;

    /**
     * Default constructor initializing a new OTP verification with default values.
     */
    public OtpVerification() {
        this.referenceNumber = UUID.randomUUID().toString();
        this.status = VerificationStatus.INITIATED;
        this.attemptCount = 0;
    }

    /**
     * Gets the unique reference number for this OTP verification.
     *
     * @return The reference number
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * Sets the unique reference number for this OTP verification.
     *
     * @param referenceNumber The reference number to set
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * Gets the hashed OTP code.
     *
     * @return The hashed OTP code
     */
    public String getOtpHash() {
        return otpHash;
    }

    /**
     * Sets the hashed OTP code.
     *
     * @param otpHash The hashed OTP code to set
     */
    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    /**
     * Gets the current status of the OTP verification.
     *
     * @return The verification status
     */
    public VerificationStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the OTP verification.
     *
     * @param status The verification status to set
     */
    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    /**
     * Gets the reason for OTP verification failure, if applicable.
     *
     * @return The failure reason
     */
    public FailureReason getFailureReason() {
        return failureReason;
    }

    /**
     * Sets the reason for OTP verification failure, if applicable.
     *
     * @param failureReason The failure reason to set
     */
    public void setFailureReason(FailureReason failureReason) {
        this.failureReason = failureReason;
    }

    /**
     * Gets the timestamp when verification was completed.
     *
     * @return The verification completion timestamp
     */
    public LocalDateTime getVerificationCompletedAt() {
        return verificationCompletedAt;
    }

    /**
     * Sets the timestamp when verification was completed.
     *
     * @param verificationCompletedAt The verification completion timestamp to set
     */
    public void setVerificationCompletedAt(LocalDateTime verificationCompletedAt) {
        this.verificationCompletedAt = verificationCompletedAt;
    }

    /**
     * Gets the expiry time for this OTP verification.
     *
     * @return The expiry timestamp
     */
    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    /**
     * Sets the expiry time for this OTP verification.
     *
     * @param expiryTime The expiry timestamp to set
     */
    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    /**
     * Gets the number of verification attempts made.
     *
     * @return The attempt count
     */
    public Integer getAttemptCount() {
        return attemptCount;
    }

    /**
     * Sets the number of verification attempts made.
     *
     * @param attemptCount The attempt count to set
     */
    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    /**
     * Gets the external transaction ID from the UIDAI system.
     *
     * @return The external transaction ID
     */
    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    /**
     * Sets the external transaction ID from the UIDAI system.
     *
     * @param externalTransactionId The external transaction ID to set
     */
    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    /**
     * Gets the hashed response from the UIDAI system.
     *
     * @return The hashed response
     */
    public String getResponseHash() {
        return responseHash;
    }

    /**
     * Sets the hashed response from the UIDAI system.
     *
     * @param responseHash The hashed response to set
     */
    public void setResponseHash(String responseHash) {
        this.responseHash = responseHash;
    }

    /**
     * Gets the eKYC request associated with this OTP verification.
     *
     * @return The associated eKYC request
     */
    public EkycRequest getEkycRequest() {
        return ekycRequest;
    }

    /**
     * Sets the eKYC request associated with this OTP verification.
     *
     * @param ekycRequest The eKYC request to associate
     */
    public void setEkycRequest(EkycRequest ekycRequest) {
        this.ekycRequest = ekycRequest;
    }

    /**
     * Increments the attempt count by 1.
     */
    public void incrementAttemptCount() {
        this.attemptCount = (this.attemptCount == null) ? 1 : this.attemptCount + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OtpVerification that = (OtpVerification) o;
        return Objects.equals(referenceNumber, that.referenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), referenceNumber);
    }

    @Override
    public String toString() {
        return "OtpVerification{" +
                "referenceNumber='" + referenceNumber + '\'' +
                ", status=" + status +
                ", attemptCount=" + attemptCount +
                '}';
    }
}