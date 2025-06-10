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
@Schema(description = "Record of a request and its response")
public class RequestAuditRecord {

    @Schema(description = "Trace ID of the request")
    private String traceId;

    @Schema(description = "Timestamp when the request was received")
    private Instant timestamp;

    @Schema(description = "Operation type (initiate/verify)")
    private String operation;

    @Schema(description = "Request data with PII masked")
    private Object request;

    @Schema(description = "Response data with PII masked")
    private Object response;

    @Schema(description = "HTTP status code of the response")
    private Integer status;

    @Schema(description = "Timestamp when the response was sent")
    private Instant responseTimestamp;
}