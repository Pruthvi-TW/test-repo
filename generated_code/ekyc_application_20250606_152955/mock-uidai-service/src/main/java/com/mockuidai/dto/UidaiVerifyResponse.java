package com.mockuidai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response for OTP verification request")
public class UidaiVerifyResponse {

    @Schema(description = "Status of the OTP verification", example = "VERIFIED", required = true)
    private String status;

    @Schema(description = "KYC data of the user")
    private KycData kycData;

    @Schema(description = "Timestamp of the response", example = "2025-05-23T10:01:00Z", required = true)
    private String timestamp;

    @Schema(description = "Error message in case of failure")
    private String errorMessage;
}