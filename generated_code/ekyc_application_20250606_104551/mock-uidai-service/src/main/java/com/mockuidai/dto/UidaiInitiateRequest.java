```java
package com.mockuidai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UidaiInitiateRequest {
    @NotBlank(message = "aadhaarOrVid is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "aadhaarOrVid must be 12 digits")
    private String aadhaarOrVid;
    
    @NotBlank(message = "transactionId is required")
    private String transactionId;
}
```