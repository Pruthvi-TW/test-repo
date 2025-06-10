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
@Schema(description = "Response for OTP verification and eKYC retrieval")
public class UidaiVerifyResponse {

    @Schema(description = "Status of the verification request", example = "VERIFIED")
    private String status;

    @Schema(description = "KYC data of the verified user")
    private KycData kycData;

    @Schema(description = "Timestamp of the response", example = "2025-05-23T10:01:00Z")
    private Instant timestamp;

    @Schema(description = "Error message in case of failure", example = "Invalid OTP")
    private String errorMessage;
}