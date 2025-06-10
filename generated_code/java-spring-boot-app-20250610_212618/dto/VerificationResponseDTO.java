package com.kyc.verification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class VerificationResponseDTO {
    @NotNull
    @JsonProperty("referenceId")
    private final String referenceId;

    @NotNull
    @JsonProperty("status")
    private final String status;

    @NotNull
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final LocalDateTime timestamp;

    @JsonProperty("errorCode")
    private final String errorCode;

    @JsonProperty("errorMessage")
    private final String errorMessage;

    @JsonProperty("kycData")
    private final Map<String, Object> kycData;

    @JsonProperty("sessionId")
    private final String sessionId;

    @JsonProperty("traceId")
    private final String traceId;

    public VerificationResponseDTO(String referenceId, String status, LocalDateTime timestamp,
                                 String errorCode, String errorMessage, Map<String, Object> kycData,
                                 String sessionId, String traceId) {
        this.referenceId = referenceId;
        this.status = status;
        this.timestamp = timestamp;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.kycData = kycData;
        this.sessionId = sessionId;
        this.traceId = traceId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, Object> getKycData() {
        return kycData;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTraceId() {
        return traceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationResponseDTO that = (VerificationResponseDTO) o;
        return Objects.equals(referenceId, that.referenceId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(errorCode, that.errorCode) &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(kycData, that.kycData) &&
                Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(traceId, that.traceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId, status, timestamp, errorCode, errorMessage, kycData, sessionId, traceId);
    }

    @Override
    public String toString() {
        return "VerificationResponseDTO{" +
                "referenceId='" + referenceId + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", kycData=" + (kycData != null ? "[MASKED]" : "null") +
                ", sessionId='" + sessionId + '\'' +
                ", traceId='" + traceId + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String referenceId;
        private String status;
        private LocalDateTime timestamp;
        private String errorCode;
        private String errorMessage;
        private Map<String, Object> kycData;
        private String sessionId;
        private String traceId;

        private Builder() {
        }

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
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

        public Builder kycData(Map<String, Object> kycData) {
            this.kycData = kycData;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public VerificationResponseDTO build() {
            if (timestamp == null) {
                timestamp = LocalDateTime.now();
            }
            
            if (referenceId == null || status == null) {
                throw new IllegalStateException("ReferenceId and status are required fields");
            }

            return new VerificationResponseDTO(
                    referenceId,
                    status,
                    timestamp,
                    errorCode,
                    errorMessage,
                    kycData,
                    sessionId,
                    traceId
            );
        }
    }
}