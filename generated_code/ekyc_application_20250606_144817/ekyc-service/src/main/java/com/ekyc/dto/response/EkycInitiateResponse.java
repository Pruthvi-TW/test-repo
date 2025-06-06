package com.ekyc.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

import com.ekyc.dto.common.BaseResponse;
import com.ekyc.enums.EkycStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response DTO for the eKYC initiation process.
 * Contains information about the initiated eKYC verification.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EkycInitiateResponse extends BaseResponse {

    private String transactionId;
    private EkycStatus ekycStatus;
    private String maskedMobileNumber;
    private String maskedEmail;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime expiryTime;
    
    private Integer otpRetryCount;
    private Integer otpRemainingAttempts;
    private String nextAction;

    // Default constructor
    public EkycInitiateResponse() {
        super();
    }

    // Constructor with status and message
    public EkycInitiateResponse(String status, String message) {
        super(status, message);
    }

    // Constructor with status, message, and referenceId
    public EkycInitiateResponse(String status, String message, String referenceId) {
        super(status, message, referenceId);
    }

    // Getters and setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public EkycStatus getEkycStatus() {
        return ekycStatus;
    }

    public void setEkycStatus(EkycStatus ekycStatus) {
        this.ekycStatus = ekycStatus;
    }

    public String getMaskedMobileNumber() {
        return maskedMobileNumber;
    }

    public void setMaskedMobileNumber(String maskedMobileNumber) {
        this.maskedMobileNumber = maskedMobileNumber;
    }

    public String getMaskedEmail() {
        return maskedEmail;
    }

    public void setMaskedEmail(String maskedEmail) {
        this.maskedEmail = maskedEmail;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public Integer getOtpRetryCount() {
        return otpRetryCount;
    }

    public void setOtpRetryCount(Integer otpRetryCount) {
        this.otpRetryCount = otpRetryCount;
    }

    public Integer getOtpRemainingAttempts() {
        return otpRemainingAttempts;
    }

    public void setOtpRemainingAttempts(Integer otpRemainingAttempts) {
        this.otpRemainingAttempts = otpRemainingAttempts;
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
               ekycStatus == that.ekycStatus &&
               Objects.equals(maskedMobileNumber, that.maskedMobileNumber) &&
               Objects.equals(maskedEmail, that.maskedEmail) &&
               Objects.equals(expiryTime, that.expiryTime) &&
               Objects.equals(otpRetryCount, that.otpRetryCount) &&
               Objects.equals(otpRemainingAttempts, that.otpRemainingAttempts) &&
               Objects.equals(nextAction, that.nextAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transactionId, ekycStatus, maskedMobileNumber, maskedEmail, expiryTime, otpRetryCount, otpRemainingAttempts, nextAction);
    }

    @Override
    public String toString() {
        return "EkycInitiateResponse{" +
                "status='" + getStatus() + '\'' +
                ", message='" + getMessage() + '\'' +
                ", referenceId='" + getReferenceId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", transactionId='" + transactionId + '\'' +
                ", ekycStatus=" + ekycStatus +
                ", maskedMobileNumber='" + maskedMobileNumber + '\'' +
                ", maskedEmail='" + maskedEmail + '\'' +
                ", expiryTime=" + expiryTime +
                ", otpRetryCount=" + otpRetryCount +
                ", otpRemainingAttempts=" + otpRemainingAttempts +
                ", nextAction='" + nextAction + '\'' +
                '}';
    }

    /**
     * Builder class for EkycInitiateResponse
     */
    public static class Builder {
        private String status;
        private String message;
        private String referenceId;
        private String transactionId;
        private EkycStatus ekycStatus;
        private String maskedMobileNumber;
        private String maskedEmail;
        private LocalDateTime expiryTime;
        private Integer otpRetryCount;
        private Integer otpRemainingAttempts;
        private String nextAction;

        public Builder status(String status) {
            this.status = status;
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

        public Builder ekycStatus(EkycStatus ekycStatus) {
            this.ekycStatus = ekycStatus;
            return this;
        }

        public Builder maskedMobileNumber(String maskedMobileNumber) {
            this.maskedMobileNumber = maskedMobileNumber;
            return this;
        }

        public Builder maskedEmail(String maskedEmail) {
            this.maskedEmail = maskedEmail;
            return this;
        }

        public Builder expiryTime(LocalDateTime expiryTime) {
            this.expiryTime = expiryTime;
            return this;
        }

        public Builder otpRetryCount(Integer otpRetryCount) {
            this.otpRetryCount = otpRetryCount;
            return this;
        }

        public Builder otpRemainingAttempts(Integer otpRemainingAttempts) {
            this.otpRemainingAttempts = otpRemainingAttempts;
            return this;
        }

        public Builder nextAction(String next