package com.kyc.verification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class EkycRequestDTO {

    @NotBlank(message = "ID is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "ID must be exactly 12 numeric digits")
    private final String id;

    @NotNull(message = "ID type is required")
    private final IdType idType;

    @NotNull(message = "Identity verification consent is mandatory")
    private final Boolean identityVerificationConsent;

    @NotNull(message = "Mobile/email consent must be specified")
    private final Boolean contactVerificationConsent;

    @NotBlank(message = "Session ID is required")
    @Size(max = 64, message = "Session ID cannot exceed 64 characters")
    private final String sessionId;

    @Size(max = 64, message = "Parent process ID cannot exceed 64 characters")
    private final String parentProcessId;

    private final String referenceNumber;

    private final VerificationStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final LocalDateTime updatedAt;

    private final String errorCode;
    private final String errorMessage;

    public EkycRequestDTO(String id, IdType idType, Boolean identityVerificationConsent,
                         Boolean contactVerificationConsent, String sessionId, String parentProcessId,
                         String referenceNumber, VerificationStatus status, LocalDateTime createdAt,
                         LocalDateTime updatedAt, String errorCode, String errorMessage) {
        this.id = id;
        this.idType = idType;
        this.identityVerificationConsent = identityVerificationConsent;
        this.contactVerificationConsent = contactVerificationConsent;
        this.sessionId = sessionId;
        this.parentProcessId = parentProcessId;
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public IdType getIdType() {
        return idType;
    }

    public Boolean getIdentityVerificationConsent() {
        return identityVerificationConsent;
    }

    public Boolean getContactVerificationConsent() {
        return contactVerificationConsent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getParentProcessId() {
        return parentProcessId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EkycRequestDTO that = (EkycRequestDTO) o;
        return Objects.equals(id, that.id) &&
                idType == that.idType &&
                Objects.equals(identityVerificationConsent, that.identityVerificationConsent) &&
                Objects.equals(contactVerificationConsent, that.contactVerificationConsent) &&
                Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(parentProcessId, that.parentProcessId) &&
                Objects.equals(referenceNumber, that.referenceNumber) &&
                status == that.status &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(errorCode, that.errorCode) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idType, identityVerificationConsent, contactVerificationConsent,
                sessionId, parentProcessId, referenceNumber, status, createdAt, updatedAt,
                errorCode, errorMessage);
    }

    @Override
    public String toString() {
        return "EkycRequestDTO{" +
                "id='" + maskPii(id) + '\'' +
                ", idType=" + idType +
                ", identityVerificationConsent=" + identityVerificationConsent +
                ", contactVerificationConsent=" + contactVerificationConsent +
                ", sessionId='" + sessionId + '\'' +
                ", parentProcessId='" + parentProcessId + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    private String maskPii(String value) {
        if (value == null || value.length() < 8) {
            return "****";
        }
        return "****" + value.substring(value.length() - 4);
    }

    public static class Builder {
        private String id;
        private IdType idType;
        private Boolean identityVerificationConsent;
        private Boolean contactVerificationConsent;
        private String sessionId;
        private String parentProcessId;
        private String referenceNumber;
        private VerificationStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String errorCode;
        private String errorMessage;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder idType(IdType idType) {
            this.idType = idType;
            return this;
        }

        public Builder identityVerificationConsent(Boolean identityVerificationConsent) {
            this.identityVerificationConsent = identityVerificationConsent;
            return this;
        }

        public Builder contactVerificationConsent(Boolean contactVerificationConsent) {
            this.contactVerificationConsent = contactVerificationConsent;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder parentProcessId(String parentProcessId) {
            this.parentProcessId = parentProcessId;
            return this;
        }

        public Builder referenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
            return this;
        }

        public Builder status(VerificationStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public EkycRequestDTO build() {
            return new EkycRequestDTO(id, idType, identityVerificationConsent,
                    contactVerificationConsent, sessionId, parentProcessId, referenceNumber,
                    status, createdAt, updatedAt, errorCode, errorMessage);
        }
    }

    public enum IdType {
        AADHAAR,
        VID
    }

    public enum VerificationStatus {
        INITIATED,
        IN_PROGRESS,
        VERIFIED,
        FAILED
    }
}