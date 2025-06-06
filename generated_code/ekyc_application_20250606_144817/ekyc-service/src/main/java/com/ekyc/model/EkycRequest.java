package com.ekyc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing an eKYC verification request.
 * This entity stores the details of a request for electronic Know Your Customer verification,
 * including the ID type, consent information, and verification status.
 *
 * @author eKYC Team
 * @version 1.0
 */
@Entity
@Table(name = "ekyc_requests")
public class EkycRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Enumeration of possible ID types for eKYC verification.
     */
    public enum IdType {
        AADHAAR, VID
    }

    /**
     * Enumeration of possible verification statuses.
     */
    public enum VerificationStatus {
        INITIATED, IN_PROGRESS, VERIFIED, FAILED, EXPIRED
    }

    /**
     * Enumeration of possible consent values.
     */
    public enum ConsentValue {
        YES, NO
    }

    @NaturalId
    @Column(name = "reference_number", nullable = false, unique = true, updatable = false, length = 36)
    private String referenceNumber;

    @Column(name = "id_number", nullable = false, length = 12)
    @NotBlank(message = "ID number is required")
    @Size(min = 12, max = 12, message = "ID number must be exactly 12 digits")
    @Pattern(regexp = "^[0-9]{12}$", message = "ID number must contain only digits")
    private String idNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "id_type", nullable = false, length = 10)
    @NotNull(message = "ID type is required")
    private IdType idType;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_verification_consent", nullable = false, length = 3)
    @NotNull(message = "Identity verification consent is required")
    private ConsentValue identityVerificationConsent;

    @Enumerated(EnumType.STRING)
    @Column(name = "mobile_email_consent", nullable = false, length = 3)
    @NotNull(message = "Mobile/email consent is required")
    private ConsentValue mobileEmailConsent;

    @Column(name = "session_id", nullable = false, length = 36)
    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @Column(name = "parent_process_id", length = 36)
    private String parentProcessId;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    @NotNull(message = "Verification status is required")
    private VerificationStatus verificationStatus;

    @Column(name = "external_reference_id", length = 100)
    private String externalReferenceId;

    @Column(name = "verification_timestamp")
    private LocalDateTime verificationTimestamp;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "expiry_timestamp")
    private LocalDateTime expiryTimestamp;

    @OneToMany(mappedBy = "ekycRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OtpVerification> otpVerifications = new ArrayList<>();

    /**
     * Gets the reference number for this eKYC request.
     *
     * @return The reference number
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * Sets the reference number for this eKYC request.
     *
     * @param referenceNumber The reference number
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * Gets the ID number (Aadhaar or VID).
     *
     * @return The ID number
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * Sets the ID number (Aadhaar or VID).
     *
     * @param idNumber The ID number
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * Gets the type of ID (AADHAAR or VID).
     *
     * @return The ID type
     */
    public IdType getIdType() {
        return idType;
    }

    /**
     * Sets the type of ID (AADHAAR or VID).
     *
     * @param idType The ID type
     */
    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    /**
     * Gets the identity verification consent value.
     *
     * @return The identity verification consent
     */
    public ConsentValue getIdentityVerificationConsent() {
        return identityVerificationConsent;
    }

    /**
     * Sets the identity verification consent value.
     *
     * @param identityVerificationConsent The identity verification consent
     */
    public void setIdentityVerificationConsent(ConsentValue identityVerificationConsent) {
        this.identityVerificationConsent = identityVerificationConsent;
    }

    /**
     * Gets the mobile/email consent value.
     *
     * @return The mobile/email consent
     */
    public ConsentValue getMobileEmailConsent() {
        return mobileEmailConsent;
    }

    /**
     * Sets the mobile/email consent value.
     *
     * @param mobileEmailConsent The mobile/email consent
     */
    public void setMobileEmailConsent(ConsentValue mobileEmailConsent) {
        this.mobileEmailConsent = mobileEmailConsent;
    }

    /**
     * Gets the session ID for this eKYC request.
     *
     * @return The session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID for this eKYC request.
     *
     * @param sessionId The session ID
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the parent process ID if this request is part of a larger process.
     *
     * @return The parent process ID
     */
    public String getParentProcessId() {
        return parentProcessId;
    }

    /**
     * Sets the parent process ID if this request is part of a larger process.
     *
     * @param parentProcessId The parent process ID
     */
    public void setParentProcessId(String parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    /**
     * Gets the current verification status of this eKYC request.
     *
     * @return The verification status
     */
    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    /**
     * Sets the current verification status of this eKYC request.
     *
     * @param verificationStatus The verification status
     */
    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    /**
     * Gets the external reference ID from the UIDAI system.
     *
     * @return The external reference ID
     */
    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    /**
     * Sets the external reference ID from the UIDAI system.
     *
     * @param externalReferenceId The external reference ID
     */
    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }

    /**
     * Gets the timestamp when verification was completed.
     *
     * @return The verification timestamp
     */
    public LocalDateTime getVerificationTimestamp() {
        return verificationTimestamp;
    }

    /**
     * Sets the timestamp when verification was completed.
     *
     * @param verificationTimestamp The verification timestamp
     */
    public void setVerificationTimestamp(LocalDateTime verificationTimestamp) {
        this.verificationTimestamp = verificationTimestamp;
    }

    /**
     * Gets the reason for verification failure, if applicable.
     *
     * @return The failure reason
     */
    public String getFailureReason() {
        return failureReason;
    }

    /**
     * Sets the reason for verification failure, if applicable.
     *
     * @param failureReason The failure reason
     */
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    /**
     * Gets the timestamp when this eKYC request expires.
     *
     * @return The expiry timestamp
     */
    public LocalDateTime getExpiryTimestamp() {
        return expiryTimestamp;
    }

    /**
     * Sets the timestamp when this eKYC request expires.
     *
     * @param expiryTimestamp The expiry timestamp
     */
    public void setExpiryTimestamp(LocalDateTime expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }

    /**
     * Gets the list of OTP verification attempts associated with this eKYC request.
     *
     * @return The list of OTP verifications
     */
    public List<OtpVerification> getOtpVerifications() {
        return otpVerifications;
    }

    /**
     * Sets the list of OTP verification attempts associated with this eKYC request.
     *
     * @param otpVerifications The list of OTP verifications
     */
    public void setOtpVerifications(List<OtpVerification> otpVerifications) {
        this.otpVerifications = otpVerifications;
    }

    /**
     * Adds an OTP verification to this eKYC request.
     *
     * @param otpVerification The OTP verification to add
     */
    public void addOtpVerification(OtpVerification otpVerification) {
        otpVerifications.add(otpVerification);
        otpVerification.setEkycRequest(this);
    }

    /**
     * Removes an OTP verification from this eKYC request.
     *
     * @param otpVerification The OTP verification to remove
     */
    public void removeOtpVerification(OtpVerification otpVerification) {
        otpVerifications.remove(otpVerification);
        otpVerification.setEkycRequest(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EkycRequest that = (EkycRequest) o;
        return Objects.equals(referenceNumber, that.referenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), referenceNumber);
    }

    @Override
    public String toString() {
        return "EkycRequest{" +
                "referenceNumber='" + referenceNumber + '\'' +
                ", idType=" + idType +
                ", verificationStatus=" + verificationStatus +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}