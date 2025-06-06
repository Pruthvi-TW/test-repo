package com.ekyc.dto.request;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Request DTO for verifying OTP during the eKYC process.
 */
public class OtpVerifyRequest {

    @NotBlank(message = "Reference ID is required")
    @Size(min = 10, max = 50, message = "Reference ID must be between 10 and 50 characters")
    private String referenceId;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be a 6-digit number")
    private String otp;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be a valid 10-digit Indian mobile number")
    private String mobileNumber;

    // Default constructor
    public OtpVerifyRequest() {
    }

    // Constructor with all fields
    public OtpVerifyRequest(String referenceId, String otp, String mobileNumber) {
        this.referenceId = referenceId;
        this.otp = otp;
        this.mobileNumber = mobileNumber;
    }

    // Getters and setters
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtpVerifyRequest that = (OtpVerifyRequest) o;
        return Objects.equals(referenceId, that.referenceId) &&
               Objects.equals(otp, that.otp) &&
               Objects.equals(mobileNumber, that.mobileNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId, otp, mobileNumber);
    }

    @Override
    public String toString() {
        // Masking sensitive information for logging
        return "OtpVerifyRequest{" +
                "referenceId='" + referenceId + '\'' +
                ", otp='[MASKED]'" +
                ", mobileNumber='[MASKED]'" +
                '}';
    }

    /**
     * Builder class for OtpVerifyRequest
     */
    public static class Builder {
        private String referenceId;
        private String otp;
        private String mobileNumber;

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder otp(String otp) {
            this.otp = otp;
            return this;
        }

        public Builder mobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public OtpVerifyRequest build() {
            OtpVerifyRequest request = new OtpVerifyRequest();
            request.setReferenceId(this.referenceId);
            request.setOtp(this.otp);
            request.setMobileNumber(this.mobileNumber);
            return request;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}