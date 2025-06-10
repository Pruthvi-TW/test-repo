```java
package com.ekyc.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.Instant;

/**
 * Base response class for standardized API responses.
 * Provides common fields for tracking and tracing responses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Reference number is mandatory")
    private String referenceNumber;

    private Instant timestamp;

    private boolean success;

    public BaseResponse() {
        this.timestamp = Instant.now();
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Builder pattern for fluent construction
    public static class Builder {
        private final BaseResponse response;

        public Builder() {
            response = new BaseResponse();
        }

        public Builder referenceNumber(String referenceNumber) {
            response.setReferenceNumber(referenceNumber);
            return this;
        }

        public Builder success(boolean success) {
            response.setSuccess(success);
            return this;
        }

        public BaseResponse build() {
            return response;
        }
    }
}
```