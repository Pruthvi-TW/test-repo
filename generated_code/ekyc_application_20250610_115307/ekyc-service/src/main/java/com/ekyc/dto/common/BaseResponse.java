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

    public BaseResponse setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public BaseResponse setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public BaseResponse setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "referenceNumber='" + referenceNumber + '\'' +
                ", timestamp=" + timestamp +
                ", success=" + success +
                '}';
    }
}
```