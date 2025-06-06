package com.mockuidai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to verify OTP and retrieve eKYC data")
public class UidaiVerifyRequest {

    @NotBlank(message = "Reference ID is required")
    @Schema(description = "Reference ID received during OTP initiation", example = "REF1234567890")
    private String referenceId;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits")
    @Schema(description = "6-digit OTP received by the user", example = "123456")
    private String otp;
}