package com.mockuidai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Request for verifying OTP for eKYC")
public class UidaiVerifyRequest {

    @NotBlank(message = "Reference ID is required")
    @Schema(description = "Reference ID received from OTP initiation", example = "REF1234567890", required = true)
    private String referenceId;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be a 6-digit number")
    @Schema(description = "6-digit OTP received by the user", example = "123456", required = true)
    private String otp;
}