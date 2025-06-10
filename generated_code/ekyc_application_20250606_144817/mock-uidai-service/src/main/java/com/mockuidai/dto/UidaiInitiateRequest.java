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
@Schema(description = "Request to initiate OTP for eKYC")
public class UidaiInitiateRequest {

    @NotBlank(message = "Aadhaar/VID is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar/VID must be 12 digits")
    @Schema(description = "12-digit Aadhaar number or VID", example = "123456789012")
    private String aadhaarOrVid;

    @NotBlank(message = "Transaction ID is required")
    @Schema(description = "Unique transaction identifier", example = "TXN100001")
    private String transactionId;
}