package com.ekyc.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ekyc.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standardized error response for API errors.
 * Contains error code, message, and details about the error.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;
    private String message;
    private ErrorCode errorCode;
    private String referenceId;
    private List<String> details;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    // Default constructor
    public ErrorResponse() {
        this.success = false;
        this.timestamp = LocalDateTime.now();
        this.details = new ArrayList<>();
    }

    // Constructor with message and error code
    public ErrorResponse(String message, ErrorCode errorCode) {
        this();
        this.message = message;
        this.errorCode = errorCode;
    }

    // Constructor with message, error code, and reference ID
    public ErrorResponse(String message, ErrorCode errorCode, String referenceId) {
        this(message, errorCode);
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

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public List<String> getDetails() {
        return Collections.unmodifiableList(details);
    }

    public void setDetails(List<String> details) {
        this.details = details != null ? new ArrayList<>(details) : new ArrayList<>();
    }

    public void addDetail(String detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);
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
        ErrorResponse that = (ErrorResponse) o;
        return success == that.success &&
                Objects.equals(message, that.message) &&
                errorCode == that.errorCode &&
                Objects.equals(referenceId, that.referenceId) &&
                Objects.equals(details, that.details) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, errorCode, referenceId, details, timestamp);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", errorCode=" + errorCode +
                ", referenceId='" + referenceId + '\'' +
                ", details=" + details +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * Builder class for ErrorResponse
     */
    public static class Builder {
        private String message;
        private ErrorCode errorCode;
        private String referenceId;
        private List<String> details = new ArrayList<>();
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder errorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder addDetail(String detail) {
            this.details.add(detail);
            return this;
        }

        public Builder details(List<String> details) {
            this.details = new ArrayList<>(details);
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorResponse build() {
            ErrorResponse response = new ErrorResponse(message, errorCode, referenceId);
            response.setDetails(details);
            response.setTimestamp(timestamp);
            return response;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}