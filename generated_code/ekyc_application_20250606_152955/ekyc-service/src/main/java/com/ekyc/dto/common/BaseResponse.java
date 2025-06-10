package com.ekyc.dto.common;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Base response class that provides common structure for all API responses.
 * Contains status, message, reference ID and timestamp fields.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
    
    private boolean success;
    private String message;
    private String referenceId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    /**
     * Default constructor initializes timestamp to current time
     */
    public BaseResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with success and message parameters
     * 
     * @param success whether the operation was successful
     * @param message response message
     */
    public BaseResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    /**
     * Constructor with all parameters
     * 
     * @param success whether the operation was successful
     * @param message response message
     * @param referenceId unique reference ID for tracking
     */
    public BaseResponse(boolean success, String message, String referenceId) {
        this(success, message);
        this.referenceId = referenceId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseResponse that = (BaseResponse) o;
        return success == that.success &&
                Objects.equals(message, that.message) &&
                Objects.equals(referenceId, that.referenceId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, referenceId, timestamp);
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * Builder class for BaseResponse
     */
    public static class Builder {
        private boolean success;
        private String message;
        private String referenceId;
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

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public BaseResponse build() {
            BaseResponse response = new BaseResponse();
            response.setSuccess(this.success);
            response.setMessage(this.message);
            response.setReferenceId(this.referenceId);
            response.setTimestamp(this.timestamp);
            return response;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}