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
@Schema(description = "Response for OTP initiation")
public class UidaiInitiateResponse {

    @Schema(description = "Status of the OTP initiation request", example = "OTP_SENT")
    private String status;

    @Schema(description = "Reference ID for OTP verification", example = "REF1234567890")
    private String referenceId;

    @Schema(description = "Timestamp of the response", example = "2025-05-23T10:00:00Z")
    private Instant timestamp;

    @Schema(description = "Error message in case of failure", example = "OTP generation failed")
    private String errorMessage;
}