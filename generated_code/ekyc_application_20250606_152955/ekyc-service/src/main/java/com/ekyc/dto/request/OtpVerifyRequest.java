package com.ekyc.dto.request;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Request DTO for OTP verification in the eKYC process.
 * Contains validated fields for OTP verification.
 */
public class OtpVerifyRequest {

    @NotBlank(message = "Reference ID is required")
    @Size(min = 10, max = 50, message = "Reference ID must be between 10 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Reference ID can only contain alphanumeric characters and hyphens")
    private String referenceId;

    @NotBlank(message = "OTP is required")
    @Size(min = 4, max = 8, message = "OTP must be between 4 and 8 characters")
    @Pattern(regexp = "^[0-9]+$", message = "OTP must contain only digits")
    private String otp;

    @NotBlank(message = "Transaction ID is required")
    @Size(min = 10, max = 50, message = "Transaction ID must be between 10 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Transaction ID can only contain alphanumeric characters and hyphens")
    private String transactionId;

    // Default constructor
    public OtpVerifyRequest() {
    }

    // All-args constructor
    public OtpVerifyRequest(String referenceId, String otp, String transactionId) {
        this.referenceId = referenceId;
        this.otp = otp;
        this.transactionId = transactionId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Returns a masked version of the OTP for logging purposes
     * @return masked OTP
     */
    @JsonIgnore
    public String getMaskedOtp() {
        if (otp == null || otp.isEmpty()) {
            return "INVALID_OTP";
        }
        return "X".repeat(otp.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtpVerifyRequest that = (OtpVerifyRequest) o;
        return Objects.equals(referenceId, that.referenceId) &&
                Objects.equals(otp, that.otp) &&
                Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId, otp, transactionId);
    }

    @Override
    public String toString() {
        return "OtpVerifyRequest{" +
                "referenceId='" + referenceId + '\'' +
                ", otp='" + getMaskedOtp() + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }

    /**
     * Builder class for OtpVerifyRequest
     */
    public static class Builder {
        private String referenceId;
        private String otp;
        private String transactionId;

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder otp(String otp) {
            this.otp = otp;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public OtpVerifyRequest build() {
            return new OtpVerifyRequest(referenceId, otp, transactionId);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}