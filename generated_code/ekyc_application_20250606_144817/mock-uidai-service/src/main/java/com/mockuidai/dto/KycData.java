package com.mockuidai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "KYC data of the verified user")
public class KycData {

    @Schema(description = "Full name of the user", example = "Ravi Kumar")
    private String name;

    @Schema(description = "Date of birth in ISO format", example = "1987-01-01")
    private LocalDate dob;

    @Schema(description = "Gender (M/F/O)", example = "M")
    private String gender;

    @Schema(description = "Address of the user", example = "123 Main Street, Bangalore, Karnataka, 560001")
    private String address;

    @Schema(description = "Mobile number (masked)", example = "98XXXX1234")
    private String maskedMobile;

    @Schema(description = "Email address (masked)", example = "ra***@gmail.com")
    private String maskedEmail;

    @Schema(description = "Photo of the user (Base64 encoded)")
    private String photo;
}