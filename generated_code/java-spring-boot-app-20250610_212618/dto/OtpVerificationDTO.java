package com.kyc.verification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class OtpVerificationDTO {

    @NotBlank(message = "Reference ID is required")
    @Size(min = 12, max = 32, message = "Reference ID must be between 12 and 32 characters")
    private final String referenceId;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be exactly 6 digits")
    private final String otp;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final LocalDateTime timestamp;

    private final String transactionId;

    private final String status;

    private final String errorCode;

    private final String errorMessage;

    private OtpVerificationDTO(Builder builder) {
        this.referenceId = builder.referenceId;
        this.otp = builder.otp;
        this.timestamp = builder.timestamp;
        this.transactionId = builder.transactionId;
        this.status = builder.status;
        this.errorCode = builder.errorCode;
        this.errorMessage = builder.errorMessage;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getOtp() {
        return otp;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getStatus() {
        return status;
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
        OtpVerificationDTO that = (OtpVerificationDTO) o;
        return Objects.equals(referenceId, that.referenceId) &&
                Objects.equals(otp, that.otp) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(errorCode, that.errorCode) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId, otp, timestamp, transactionId, status, errorCode, errorMessage);
    }

    @Override
    public String toString() {
        // Mask sensitive data in toString() for logging purposes
        return "OtpVerificationDTO{" +
                "referenceId='" + referenceId + '\'' +
                ", otp='******'" +
                ", timestamp=" + timestamp +
                ", transactionId='" + transactionId + '\'' +
                ", status='" + status + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String referenceId;
        private String otp;
        private LocalDateTime timestamp;
        private String transactionId;
        private String status;
        private String errorCode;
        private String errorMessage;

        private Builder() {
            this.timestamp = LocalDateTime.now();
        }

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder otp(String otp) {
            this.otp = otp;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
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

        public OtpVerificationDTO build() {
            return new OtpVerificationDTO(this);
        }
    }
}