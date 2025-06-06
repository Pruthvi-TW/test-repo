```java
package com.mockuidai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UidaiInitiateRequest {
    @NotBlank(message = "Aadhaar/VID is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "Invalid Aadhaar/VID format")
    private String aadhaarOrVid;
    
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
}
```