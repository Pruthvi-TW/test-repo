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
import java.util.UUID;

/**
 * Entity representing an eKYC verification request.
 * This entity stores the details of a request for electronic Know Your Customer verification,
 * including the identity document details, consent information, and verification status.
 *
 * @author eKYC Team
 * @version 1.0.0
 */
@Entity
@Table(name = "ekyc_requests", indexes = {
        @Index(name = "idx_ekyc_request_reference_number", columnList = "reference_number"),
        @Index(name = "idx_ekyc_request_session_id", columnList = "session_id"),
        @Index(name = "idx_ekyc_request_status", columnList = "status"),
        @Index(name = "idx_ekyc_request_created_at", columnList = "created_at")
})
public class EkycRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Enum representing the type of identity document used for verification.
     */
    public enum IdType {
        AADHAAR, VID
    }

    /**
     * Enum representing the consent status for accessing resident's mobile/email.
     */
    public enum ConsentStatus {
        YES, NO
    }

    /**
     * Enum representing the current status of the eKYC verification request.
     */
    public enum VerificationStatus {
        INITIATED, IN_PROGRESS, VERIFIED, FAILED, EXPIRED
    }

    @NotBlank
    @Size(min = 12, max = 12)
    @Pattern(regexp = "^[0-9]{12}$", message = "ID must be exactly 12 numeric digits")
    @Column(name = "identity_number", length = 255, nullable = false)
    private String identityNumber; // Stored encrypted

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "id_type", length = 10, nullable = false)
    private IdType idType;

    @NotNull
    @Column(name = "identity_verification_consent", nullable = false)
    private Boolean identityVerificationConsent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mobile_email_consent", length = 3, nullable = false)
    private ConsentStatus mobileEmailConsent;

    @NotBlank
    @Column(name = "session_id", length = 100, nullable = false)
    private String sessionId;

    @Column(name = "parent_process_id", length = 100)
    private String parentProcessId;

    @NaturalId
    @Column(name = "reference_number", length = 36, nullable = false, unique = true)
    private String referenceNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private VerificationStatus status;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "external_reference_id", length = 100)
    private String externalReferenceId;

    @Column(name = "verification_completed_at")
    private LocalDateTime verificationCompletedAt;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @OneToMany(mappedBy = "ekycRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OtpVerification> otpVerifications = new ArrayList<>();

    /**
     * Default constructor initializing a new eKYC request with default values.
     */
    public EkycRequest() {
        this.referenceNumber = UUID.randomUUID().toString();
        this.status = VerificationStatus.INITIATED;
    }

    /**
     * Gets the identity number (Aadhaar or VID) for verification.
     * This value is stored in encrypted form.
     *
     * @return The encrypted identity number
     */
    public String getIdentityNumber() {
        return identityNumber;
    }

    /**
     * Sets the identity number (Aadhaar or VID) for verification.
     * This value will be stored in encrypted form.
     *
     * @param identityNumber The identity number to set
     */
    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    /**
     * Gets the type of identity document used for verification.
     *
     * @return The identity document type (AADHAAR or VID)
     */
    public IdType getIdType() {
        return idType;
    }

    /**
     * Sets the type of identity document used for verification.
     *
     * @param idType The identity document type to set
     */
    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    /**
     * Gets the consent status for identity verification.
     *
     * @return True if consent was given, false otherwise
     */
    public Boolean getIdentityVerificationConsent() {
        return identityVerificationConsent;
    }

    /**
     * Sets the consent status for identity verification.
     *
     * @param identityVerificationConsent The consent status to set
     */
    public void setIdentityVerificationConsent(Boolean identityVerificationConsent) {
        this.identityVerificationConsent = identityVerificationConsent;
    }

    /**
     * Gets the consent status for accessing resident's mobile/email.
     *
     * @return The consent status (YES or NO)
     */
    public ConsentStatus getMobileEmailConsent() {
        return mobileEmailConsent;
    }

    /**
     * Sets the consent status for accessing resident's mobile/email.
     *
     * @param mobileEmailConsent The consent status to set
     */
    public void setMobileEmailConsent(ConsentStatus mobileEmailConsent) {
        this.mobileEmailConsent = mobileEmailConsent;
    }

    /**
     * Gets the session ID for tracing the interaction.
     *
     * @return The session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID for tracing the interaction.
     *
     * @param sessionId The session ID to set
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
     * @param parentProcessId The parent process ID to set
     */
    public void setParentProcessId(String parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    /**
     * Gets the unique reference number for this eKYC request.
     *
     * @return The reference number
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * Sets the unique reference number for this eKYC request.
     *
     * @param referenceNumber The reference number to set
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * Gets the current status of the verification request.
     *
     * @return The verification status
     */
    public VerificationStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the verification request.
     *
     * @param status The verification status to set
     */
    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    /**
     * Gets the error code if verification failed.
     *
     * @return The error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code if verification failed.
     *
     * @param errorCode The error code to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the error message if verification failed.
     *
     * @return The error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message if verification failed.
     *
     * @param errorMessage The error message to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
     * @param externalReferenceId The external reference ID to set
     */
    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
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
     * Gets the expiry time for this verification request.
     *
     * @return The expiry timestamp
     */
    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    /**
     * Sets the expiry time for this verification request.
     *
     * @param expiryTime The expiry timestamp to set
     */
    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    /**
     * Gets the list of OTP verification attempts associated with this request.
     *
     * @return The list of OTP verification attempts
     */
    public List<OtpVerification> getOtpVerifications() {
        return otpVerifications;
    }

    /**
     * Sets the list of OTP verification attempts associated with this request.
     *
     * @param otpVerifications The list of OTP verification attempts to set
     */
    public void setOtpVerifications(List<OtpVerification> otpVerifications) {
        this.otpVerifications = otpVerifications;
    }

    /**
     * Adds an OTP verification attempt to this request.
     *
     * @param otpVerification The OTP verification to add
     */
    public void addOtpVerification(OtpVerification otpVerification) {
        otpVerifications.add(otpVerification);
        otpVerification.setEkycRequest(this);
    }

    /**
     * Removes an OTP verification attempt from this request.
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
                ", status=" + status +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}