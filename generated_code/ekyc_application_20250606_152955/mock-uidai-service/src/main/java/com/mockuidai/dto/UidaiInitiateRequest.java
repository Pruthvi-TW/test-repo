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
@Schema(description = "Request for initiating OTP for eKYC")
public class UidaiInitiateRequest {

    @NotBlank(message = "Aadhaar/VID is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar/VID must be a 12-digit number")
    @Schema(description = "12-digit Aadhaar number or Virtual ID", example = "123456789012", required = true)
    private String aadhaarOrVid;

    @NotBlank(message = "Transaction ID is required")
    @Schema(description = "Unique transaction identifier", example = "TXN100001", required = true)
    private String transactionId;
}