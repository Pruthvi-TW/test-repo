package com.mockuidai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API request/response audit record")
public class ApiAuditRecord {

    @Schema(description = "Trace ID for the request", example = "550e8400-e29b-41d4-a716-446655440000")
    private String traceId;

    @Schema(description = "Timestamp of the request", example = "2025-05-23T10:00:00Z")
    private String timestamp;

    @Schema(description = "API operation", example = "OTP Initiation")
    private String operation;

    @Schema(description = "Request details with PII masked")
    private Object maskedRequest;

    @Schema(description = "Response details with PII masked")
    private Object maskedResponse;

    @Schema(description = "HTTP status code", example = "200")
    private Integer statusCode;

    @Schema(description = "Processing time in milliseconds", example = "350")
    private Long processingTimeMs;
}