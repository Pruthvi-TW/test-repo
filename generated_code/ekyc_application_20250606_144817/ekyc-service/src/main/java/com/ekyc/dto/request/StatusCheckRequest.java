package com.ekyc.dto.request;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Request DTO for checking the status of an eKYC verification process.
 */
public class StatusCheckRequest {

    @NotBlank(message = "Reference ID is required")
    @Size(min = 10, max = 50, message = "Reference ID must be between 10 and 50 characters")
    private String referenceId;

    // Default constructor
    public StatusCheckRequest() {
    }

    // Constructor with referenceId
    public StatusCheckRequest(String referenceId) {
        this.referenceId = referenceId;
    }

    // Getters and setters
    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusCheckRequest that = (StatusCheckRequest) o;
        return Objects.equals(referenceId, that.referenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId);
    }

    @Override
    public String toString() {
        return "StatusCheckRequest{" +
                "referenceId='" + referenceId + '\'' +
                '}';
    }

    /**
     * Builder class for StatusCheckRequest
     */
    public static class Builder {
        private String referenceId;

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public StatusCheckRequest build() {
            StatusCheckRequest request = new StatusCheckRequest();
            request.setReferenceId(this.referenceId);
            return request;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}