package com.mockuidai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Admin configuration request")
public class AdminConfigRequest {

    @Schema(description = "Multiplier for response latency (1.0 = normal)", example = "1.5")
    private Double latencyMultiplier;

    @Schema(description = "Force system error for all requests", example = "false")
    private Boolean forceSystemError;

    @Schema(description = "Force OTP failure for all requests", example = "false")
    private Boolean forceOtpFailure;

    @Schema(description = "Force expired OTP for all verification requests", example = "false")
    private Boolean forceExpiredOtp;

    @Schema(description = "Custom OTP value that will be considered valid", example = "123456")
    private String customOtpValue;
}