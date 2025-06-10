package com.kyc.verification.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ekyc_requests")
public class EkycRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "reference_number", unique = true, nullable = false, updatable = false)
    private String referenceNumber;

    @NotNull
    @Pattern(regexp = "\\d{12}", message = "ID must be exactly 12 digits")
    @Column(name = "aadhaar_vid", nullable = false, updatable = false)
    private String aadhaarOrVid;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "id_type", nullable = false, updatable = false)
    private IdType idType;

    @NotNull
    @Column(name = "identity_verification_consent", nullable = false, updatable = false)
    private boolean identityVerificationConsent;

    @NotNull
    @Column(name = "contact_verification_consent", nullable = false, updatable = false)
    private boolean contactVerificationConsent;

    @NotNull
    @Column(name = "session_id", nullable = false, updatable = false)
    private String sessionId;

    @Column(name = "parent_process_id")
    private String parentProcessId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VerificationStatus status;

    @Column(name = "error_details")
    private String errorDetails;

    @Column(name = "uidai_reference")
    private String uidaiReference;

    @OneToMany(mappedBy = "ekycRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OtpVerification> otpVerifications = new ArrayList<>();

    @OneToOne(mappedBy = "ekycRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private VerificationConsent verificationConsent;

    @OneToMany(mappedBy = "ekycRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditLog> auditLogs = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public enum IdType {
        AADHAAR, VID
    }

    public enum VerificationStatus {
        INITIATED, IN_PROGRESS, VERIFIED, FAILED
    }

    // Constructor with required fields
    public EkycRequest(String aadhaarOrVid, IdType idType, boolean identityVerificationConsent,
                      boolean contactVerificationConsent, String sessionId) {
        this.referenceNumber = generateReferenceNumber();
        this.aadhaarOrVid = aadhaarOrVid;
        this.idType = idType;
        this.identityVerificationConsent = identityVerificationConsent;
        this.contactVerificationConsent = contactVerificationConsent;
        this.sessionId = sessionId;
        this.status = VerificationStatus.INITIATED;
    }

    // Default constructor for JPA
    protected EkycRequest() {
    }

    private String generateReferenceNumber() {
        return "EKYC" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getAadhaarOrVid() {
        return aadhaarOrVid;
    }

    public IdType getIdType() {
        return idType;
    }

    public boolean isIdentityVerificationConsent() {
        return identityVerificationConsent;
    }

    public boolean isContactVerificationConsent() {
        return contactVerificationConsent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getParentProcessId() {
        return parentProcessId;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public String getUidaiReference() {
        return uidaiReference;
    }

    public List<OtpVerification> getOtpVerifications() {
        return new ArrayList<>(otpVerifications);
    }

    public VerificationConsent getVerificationConsent() {
        return verificationConsent;
    }

    public List<AuditLog> getAuditLogs() {
        return new ArrayList<>(auditLogs);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    // Setters for mutable fields
    public void setParentProcessId(String parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public void setUidaiReference(String uidaiReference) {
        this.uidaiReference = uidaiReference;
    }

    // Relationship management methods
    public void addOtpVerification(OtpVerification otpVerification) {
        otpVerifications.add(otpVerification);
        otpVerification.setEkycRequest(this);
    }

    public void setVerificationConsent(VerificationConsent verificationConsent) {
        this.verificationConsent = verificationConsent;
        verificationConsent.setEkycRequest(this);
    }

    public void addAuditLog(AuditLog auditLog) {
        auditLogs.add(auditLog);
        auditLog.setEkycRequest(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EkycRequest that = (EkycRequest) o;
        return Objects.equals(referenceNumber, that.referenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceNumber);
    }

    @Override
    public String toString() {
        return "EkycRequest{" +
                "referenceNumber='" + referenceNumber + '\'' +
                ", idType=" + idType +
                ", status=" + status +
                ", sessionId='" + sessionId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}