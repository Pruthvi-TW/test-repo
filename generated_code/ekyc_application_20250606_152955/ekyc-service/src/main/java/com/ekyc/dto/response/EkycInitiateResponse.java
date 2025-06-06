package com.ekyc.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

import com.ekyc.dto.common.BaseResponse;
import com.ekyc.enums.EkycStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response DTO for eKYC initiation process.
 * Extends BaseResponse to include common response fields.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EkycInitiateResponse extends BaseResponse {

    private String transactionId;
    private EkycStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime expiryTime;
    
    private String otpSentTo;
    private Integer otpLength;
    private Integer maxRetries;
    private Integer remainingRetries;
    private String nextAction;

    // Default constructor
    public EkycInitiateResponse() {
        super();
    }

    // Constructor with success and message
    public EkycInitiateResponse(boolean success, String message) {
        super(success, message);
    }

    // Constructor with success, message, and referenceId
    public EkycInitiateResponse(boolean success, String message, String referenceId) {
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

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getOtpSentTo() {
        return otpSentTo;
    }

    public void setOtpSentTo(String otpSentTo) {
        this.otpSentTo = otpSentTo;
    }

    public Integer getOtpLength() {
        return otpLength;
    }

    public void setOtpLength(Integer otpLength) {
        this.otpLength = otpLength;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EkycInitiateResponse that = (EkycInitiateResponse) o;
        return Objects.equals(transactionId, that.transactionId) &&
                status == that.status &&
                Objects.equals(expiryTime, that.expiryTime) &&
                Objects.equals(otpSentTo, that.otpSentTo) &&
                Objects.equals(otpLength, that.otpLength) &&
                Objects.equals(maxRetries, that.maxRetries) &&
                Objects.equals(remainingRetries, that.remainingRetries) &&
                Objects.equals(nextAction, that.nextAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transactionId, status, expiryTime, otpSentTo, otpLength, maxRetries, remainingRetries, nextAction);
    }

    @Override
    public String toString() {
        return "EkycInitiateResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", status=" + status +
                ", expiryTime=" + expiryTime +
                ", otpSentTo='" + otpSentTo + '\'' +
                ", otpLength=" + otpLength +
                ", maxRetries=" + maxRetries +
                ", remainingRetries=" + remainingRetries +
                ", nextAction='" + nextAction + '\'' +
                "} " + super.toString();
    }

    /**
     * Builder class for EkycInitiateResponse
     */
    public static class Builder {
        private boolean success;
        private String message;
        private String referenceId;
        private String transactionId;
        private EkycStatus status;
        private LocalDateTime expiryTime;
        private String otpSentTo;
        private Integer otpLength;
        private Integer maxRetries;
        private Integer remainingRetries;
        private String nextAction;
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

        public Builder expiryTime(LocalDateTime expiryTime) {
            this.expiryTime = expiryTime;
            return this;
        }

        public Builder otpSentTo(String otpSentTo) {
            this.otpSentTo = otpSentTo;
            return this;
        }

        public Builder otpLength(Integer otpLength) {
            this.otpLength = otpLength;
            return this;
        }

        public Builder maxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
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

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public EkycInitiateResponse build() {
            EkycInitiateResponse response = new EkycInitiateResponse(success, message, referenceId);
            response.setTransactionId(transactionId);
            response.setStatus(status);
            response.setExpiryTime(expiryTime);
            response.setOtpSentTo(otpSentTo);
            response.setOtpLength(otpLength);
            response.setMaxRetries(maxRetries);
            response.setRemainingRetries(remainingRetries);
            response.setNextAction(nextAction);
            response.setTimestamp(timestamp);
            return response;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}