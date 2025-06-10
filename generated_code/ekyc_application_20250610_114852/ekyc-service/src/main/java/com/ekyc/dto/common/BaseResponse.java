```java
package com.ekyc.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Base response class for standardized API responses.
 * Provides common fields for tracking and tracing API interactions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Transaction reference cannot be null")
    private final String transactionReference;

    @NotNull(message = "Timestamp cannot be null")
    private final Instant timestamp;

    private final boolean success;

    /**
     * Private constructor to enforce builder usage.
     */
    private BaseResponse(Builder builder) {
        this.transactionReference = builder.transactionReference;
        this.timestamp = builder.timestamp;
        this.success = builder.success;
    }

    // Getters
    public String getTransactionReference() {
        return transactionReference;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * Builder pattern for BaseResponse.
     */
    public static class Builder {
        private String transactionReference = UUID.randomUUID().toString();
        private Instant timestamp = Instant.now();
        private boolean success = true;

        public Builder transactionReference(String transactionReference) {
            this.transactionReference = transactionReference;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public BaseResponse build() {
            return new BaseResponse(this);
        }
    }

    /**
     * Static method to create a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }
}
```