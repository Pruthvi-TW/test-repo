package com.ekyc.dto.request;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Request DTO for checking the status of an eKYC process.
 * Contains validated fields for status check.
 */
public class StatusCheckRequest {

    @NotBlank(message = "Reference ID is required")
    @Size(min = 10, max = 50, message = "Reference ID must be between 10 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Reference ID can only contain alphanumeric characters and hyphens")
    private String referenceId;

    @NotBlank(message = "Customer ID is required")
    @Size(min = 5, max = 50, message = "Customer ID must be between 5 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Customer ID can only contain alphanumeric characters, hyphens, and underscores")
    private String customerId;

    // Default constructor
    public StatusCheckRequest() {
    }

    // All-args constructor
    public StatusCheckRequest(String referenceId, String customerId) {
        this.referenceId = referenceId;
        this.customerId = customerId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusCheckRequest that = (StatusCheckRequest) o;
        return Objects.equals(referenceId, that.referenceId) &&
                Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId, customerId);
    }

    @Override
    public String toString() {
        return "StatusCheckRequest{" +
                "referenceId='" + referenceId + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }

    /**
     * Builder class for StatusCheckRequest
     */
    public static class Builder {
        private String referenceId;
        private String customerId;

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public StatusCheckRequest build() {
            return new StatusCheckRequest(referenceId, customerId);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}