package com.ekyc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an OTP verification attempt for an eKYC request.
 * This entity stores the details of an OTP verification, including the OTP code,
 * verification status, and timestamps.
 *
 * @author eKYC Team
 * @version 1.0
 */
@Entity
@Table(name = "otp_verifications")
public class OtpVerification extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Enumeration of possible OTP verification statuses.
     */
    public enum OtpStatus {
        INITIATED, VERIFIED, FAILED, EXPIRED
    }

    /**
     * Enumeration of possible OTP failure reasons.
     */
    public enum OtpFailureReason {
        INVALID_OTP, EXPIRED_OTP, EXCEEDED_ATTEMPTS, TECHNICAL_ERROR
    }

    @Column(name = "otp_code", length = 6)
    @Size(min = 6, max = 6, message = "OTP code must be exactly 6 digits")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP code must contain only digits")
    private String otpCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ekyc_request_id", nullable = false)
    @NotNull(message = "eKYC request is required")
    private EkycRequest ekycRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "otp_status", nullable = false, length = 20)
    @NotNull(message = "OTP status is required")
    private OtpStatus otpStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_reason", length = 30)
    private OtpFailureReason failureReason;

    @Column(name = "verification_timestamp")
    private LocalDateTime verificationTimestamp;

    @Column(name = "expiry_timestamp")
    private LocalDateTime expiryTimestamp;

    @Column(name = "attempt_number")
    private Integer attemptNumber;

    @Column(name = "external_transaction_id", length = 100)
    private String externalTransactionId;

    @Column(name = "response_hash", length = 255)
    private String responseHash;

    /**
     * Gets the OTP code.
     *
     * @return The OTP code
     */
    public String getOtpCode() {
        return otpCode;
    }

    /**
     * Sets the OTP code.
     *
     * @param otpCode The OTP code
     */
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    /**
     * Gets the associated eKYC request.
     *
     * @return The eKYC request
     */
    public EkycRequest getEkycRequest() {
        return ekycRequest;
    }

    /**
     * Sets the associated eKYC request.
     *
     * @param ekycRequest The eKYC request
     */
    public void setEkycRequest(EkycRequest ekycRequest) {
        this.ekycRequest = ekycRequest;
    }

    /**
     * Gets the OTP verification status.
     *
     * @return The OTP status
     */
    public OtpStatus getOtpStatus() {
        return otpStatus;
    }

    /**
     * Sets the OTP verification status.
     *
     * @param otpStatus The OTP status
     */
    public void setOtpStatus(OtpStatus otpStatus) {
        this.otpStatus = otpStatus;
    }

    /**
     * Gets the reason for OTP verification failure, if applicable.
     *
     * @return The failure reason
     */
    public OtpFailureReason getFailureReason() {
        return failureReason;
    }

    /**
     * Sets the reason for OTP verification failure, if applicable.
     *
     * @param failureReason The failure reason
     */
    public void setFailureReason(OtpFailureReason failureReason) {
        this.failureReason = failureReason;
    }

    /**
     * Gets the timestamp when OTP verification was completed.
     *
     * @return The verification timestamp
     */
    public LocalDateTime getVerificationTimestamp() {
        return verificationTimestamp;
    }

    /**
     * Sets the timestamp when OTP verification was completed.
     *
     * @param verificationTimestamp The verification timestamp
     */
    public void setVerificationTimestamp(LocalDateTime verificationTimestamp) {
        this.verificationTimestamp = verificationTimestamp;
    }

    /**
     * Gets the timestamp when this OTP expires.
     *
     * @return The expiry timestamp
     */
    public LocalDateTime getExpiryTimestamp() {
        return expiryTimestamp;
    }

    /**
     * Sets the timestamp when this OTP expires.
     *
     * @param expiryTimestamp The expiry timestamp
     */
    public void setExpiryTimestamp(LocalDateTime expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }

    /**
     * Gets the attempt number for this OTP verification.
     *
     * @return The attempt number
     */
    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    /**
     * Sets the attempt number for this OTP verification.
     *
     * @param attemptNumber The attempt number
     */
    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
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
     * @param externalTransactionId The external transaction ID
     */
    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    /**
     * Gets the hash of the response from the UIDAI system.
     * This is used for audit purposes without storing PII.
     *
     * @return The response hash
     */
    public String getResponseHash() {
        return responseHash;
    }

    /**
     * Sets the hash of the response from the UIDAI system.
     * This is used for audit purposes without storing PII.
     *
     * @param responseHash The response hash
     */
    public void setResponseHash(String responseHash) {
        this.responseHash = responseHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OtpVerification that = (OtpVerification) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }

    @Override
    public String toString() {
        return "OtpVerification{" +
                "id=" + getId() +
                ", otpStatus=" + otpStatus +
                ", attemptNumber=" + attemptNumber +
                '}';
    }
}