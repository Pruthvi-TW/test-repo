package com.kyc.verification.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "verification_consent")
public class VerificationConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consent_reference", nullable = false, unique = true, updatable = false)
    private String consentReference = UUID.randomUUID().toString();

    @NotNull(message = "Identity verification consent is mandatory")
    @Column(name = "identity_verification_consent", nullable = false, updatable = false)
    private boolean identityVerificationConsent;

    @NotNull(message = "Contact verification consent must be explicitly set")
    @Pattern(regexp = "^(YES|NO)$", message = "Contact verification consent must be either 'YES' or 'NO'")
    @Column(name = "contact_verification_consent", nullable = false, updatable = false)
    private String contactVerificationConsent;

    @Size(max = 500)
    @Column(name = "consent_details")
    private String consentDetails;

    @Column(name = "consent_timestamp", nullable = false, updatable = false)
    private LocalDateTime consentTimestamp;

    @Column(name = "consent_ip_address")
    private String consentIpAddress;

    @Column(name = "consent_user_agent")
    private String consentUserAgent;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ekyc_request_id", nullable = false)
    private EkycRequest ekycRequest;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    public VerificationConsent() {
        this.consentTimestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getConsentReference() {
        return consentReference;
    }

    public boolean isIdentityVerificationConsent() {
        return identityVerificationConsent;
    }

    public void setIdentityVerificationConsent(boolean identityVerificationConsent) {
        this.identityVerificationConsent = identityVerificationConsent;
    }

    public String getContactVerificationConsent() {
        return contactVerificationConsent;
    }

    public void setContactVerificationConsent(String contactVerificationConsent) {
        this.contactVerificationConsent = contactVerificationConsent;
    }

    public String getConsentDetails() {
        return consentDetails;
    }

    public void setConsentDetails(String consentDetails) {
        this.consentDetails = consentDetails;
    }

    public LocalDateTime getConsentTimestamp() {
        return consentTimestamp;
    }

    public String getConsentIpAddress() {
        return consentIpAddress;
    }

    public void setConsentIpAddress(String consentIpAddress) {
        this.consentIpAddress = consentIpAddress;
    }

    public String getConsentUserAgent() {
        return consentUserAgent;
    }

    public void setConsentUserAgent(String consentUserAgent) {
        this.consentUserAgent = consentUserAgent;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public EkycRequest getEkycRequest() {
        return ekycRequest;
    }

    public void setEkycRequest(EkycRequest ekycRequest) {
        this.ekycRequest = ekycRequest;
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

    @PrePersist
    protected void onCreate() {
        if (this.expiryDate == null) {
            // Default consent validity for 90 days
            this.expiryDate = LocalDateTime.now().plusDays(90);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationConsent that = (VerificationConsent) o;
        return Objects.equals(consentReference, that.consentReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consentReference);
    }

    @Override
    public String toString() {
        return "VerificationConsent{" +
                "id=" + id +
                ", consentReference='" + consentReference + '\'' +
                ", identityVerificationConsent=" + identityVerificationConsent +
                ", contactVerificationConsent='" + contactVerificationConsent + '\'' +
                ", consentTimestamp=" + consentTimestamp +
                ", active=" + active +
                ", expiryDate=" + expiryDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}