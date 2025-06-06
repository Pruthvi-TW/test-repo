```java
package com.mockuidai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UidaiVerifyRequest {
    @NotBlank(message = "Reference ID is required")
    private String referenceId;
    
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Invalid OTP format")
    private String otp;
}