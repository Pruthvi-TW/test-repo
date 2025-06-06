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
@Schema(description = "KYC data of the user")
public class KycData {

    @Schema(description = "Full name of the user", example = "Ravi Kumar", required = true)
    private String name;

    @Schema(description = "Date of birth in YYYY-MM-DD format", example = "1987-01-01", required = true)
    private String dob;

    @Schema(description = "Gender (M/F/O)", example = "M", required = true)
    private String gender;

    @Schema(description = "Address of the user")
    private String address;

    @Schema(description = "Mobile number (masked)", example = "XXXXXXX123")
    private String maskedMobile;

    @Schema(description = "Email address (masked)", example = "r***@g****.com")
    private String maskedEmail;

    @Schema(description = "Photo of the user (Base64 encoded)")
    private String photo;
}