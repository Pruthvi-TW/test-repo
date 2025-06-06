package com.ekyc.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

import com.ekyc.dto.common.BaseResponse;
import com.ekyc.enums.EkycStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response DTO for OTP verification in the eKYC process.
 * Extends BaseResponse to include common response fields.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtpVerifyResponse extends BaseResponse {

    private String transactionId;
    private EkycStatus status;
    private Integer remainingRetries;
    private String nextAction;
    private KycDataResponse kycData;

    // Default constructor
    public OtpVerifyResponse() {
        super();
    }

    // Constructor with success and message
    public OtpVerifyResponse(boolean success, String message) {
        super(success, message);
    }

    // Constructor with success, message, and referenceId
    public OtpVerifyResponse(boolean success, String message, String referenceId) {
        super(success, message, referenceId);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public EkycStatus getStatus() {
        return status;
    }

    public void setStatus(EkycStatus status) {
        this.status = status;
    }

    public Integer getRemainingRetries() {
        return remainingRetries;
    }

    public void setRemainingRetries(Integer remainingRetries) {
        this.remainingRetries = remainingRetries;
    }

    public String getNextAction() {
        return nextAction;
    }

    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }

    public KycDataResponse getKycData() {
        return kycData;
    }

    public void setKycData(KycDataResponse kycData) {
        this.kycData = kycData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OtpVerifyResponse that = (OtpVerifyResponse) o;
        return Objects.equals(transactionId, that.transactionId) &&
                status == that.status &&
                Objects.equals(remainingRetries, that.remainingRetries) &&
                Objects.equals(nextAction, that.nextAction) &&
                Objects.equals(kycData, that.kycData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transactionId, status, remainingRetries, nextAction, kycData);
    }

    @Override
    public String toString() {
        return "OtpVerifyResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", status=" + status +
                ", remainingRetries=" + remainingRetries +
                ", nextAction='" + nextAction + '\'' +
                ", kycData=" + (kycData != null ? "PRESENT" : "NULL") +
                "} " + super.toString();
    }

    /**
     * Builder class for OtpVerifyResponse
     */
    public static class Builder {
        private boolean success;
        private String message;
        private String referenceId;
        private String transactionId;
        private EkycStatus status;
        private Integer remainingRetries;
        private String nextAction;
        private KycDataResponse kycData;
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder status(EkycStatus status) {
            this.status = status;
            return this;
        }

        public Builder remainingRetries(Integer remainingRetries) {
            this.remainingRetries = remainingRetries;
            return this;
        }

        public Builder nextAction(String nextAction) {
            this.nextAction = nextAction;
            return this;
        }

        public Builder kycData(KycDataResponse kycData) {
            this.kycData = kycData;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public OtpVerifyResponse build() {
            OtpVerifyResponse response = new OtpVerifyResponse(success, message, referenceId);
            response.setTransactionId(transactionId);
            response.setStatus(status);
            response.setRemainingRetries(remainingRetries);
            response.setNextAction(nextAction);
            response.setKycData(kycData);
            response.setTimestamp(timestamp);
            return response;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}