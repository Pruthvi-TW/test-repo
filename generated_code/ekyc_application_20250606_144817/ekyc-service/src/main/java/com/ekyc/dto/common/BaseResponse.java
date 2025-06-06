package com.ekyc.dto.common;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Base response class that all response DTOs should extend.
 * Provides common fields for all API responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
    
    private String status;
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
     * Constructor with status and message
     * 
     * @param status Response status code
     * @param message Response message
     */
    public BaseResponse(String status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    /**
     * Constructor with all fields
     * 
     * @param status Response status code
     * @param message Response message
     * @param referenceId Unique reference ID for tracking
     */
    public BaseResponse(String status, String message, String referenceId) {
        this(status, message);
        this.referenceId = referenceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return Objects.equals(status, that.status) &&
               Objects.equals(message, that.message) &&
               Objects.equals(referenceId, that.referenceId) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, referenceId, timestamp);
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * Builder class for BaseResponse
     */
    public static class Builder {
        private String status;
        private String message;
        private String referenceId;
        private LocalDateTime timestamp = LocalDateTime.now();

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

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public BaseResponse build() {
            BaseResponse response = new BaseResponse();
            response.setStatus(this.status);
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