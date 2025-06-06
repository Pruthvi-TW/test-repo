package com.ekyc.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

import com.ekyc.dto.common.BaseResponse;
import com.ekyc.enums.EkycStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response DTO for checking the status of an eKYC process.
 * Extends BaseResponse to include common response fields.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusCheckResponse extends BaseResponse {

    private String transactionId;
    private EkycStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime completionTime;
    
    private String nextAction;
    private KycDataResponse kycData;
    private String failureReason;

    // Default constructor
    public StatusCheckResponse() {
        super();
    }

    // Constructor with success and message
    public StatusCheckResponse(boolean success, String message) {
        super(success, message);
    }

    // Constructor with success, message, and referenceId
    public StatusCheckResponse(boolean success, String message, String referenceId) {
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

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
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

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StatusCheckResponse that = (StatusCheckResponse) o;
        return Objects.equals(transactionId, that.transactionId) &&
                status == that.status &&
                Objects.equals(completionTime, that.completionTime) &&
                Objects.equals(nextAction, that.nextAction) &&
                Objects.equals(kycData, that.kycData) &&
                Objects.equals(failureReason, that.failureReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transactionId, status, completionTime, nextAction, kycData, failureReason);
    }

    @Override
    public String toString() {
        return "StatusCheckResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", status=" + status +
                ", completionTime=" + completionTime +
                ", nextAction='" + nextAction + '\'' +
                ", kycData=" + (kycData != null ? "PRESENT" : "NULL") +
                ", failureReason='" + failureReason + '\'' +
                "} " + super.toString();
    }

    /**
     * Builder class for StatusCheckResponse
     */
    public static class Builder {
        private boolean success;
        private String message;
        private String referenceId;
        private String transactionId;
        private EkycStatus status;
        private LocalDateTime completionTime;
        private String nextAction;
        private KycDataResponse kycData;
        private String failureReason;
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

        public Builder completionTime(LocalDateTime completionTime) {
            this.completionTime = completionTime;
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

        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public StatusCheckResponse build() {
            StatusCheckResponse response = new StatusCheckResponse(success, message, referenceId);
            response.setTransactionId(transactionId);
            response.setStatus(status);
            response.setCompletionTime(completionTime);
            response.setNextAction(nextAction);
            response.setKycData(kycData);
            response.setFailureReason(failureReason);
            response.setTimestamp(timestamp);
            return response;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}